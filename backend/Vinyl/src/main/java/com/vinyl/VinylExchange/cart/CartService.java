package com.vinyl.VinylExchange.cart;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.vinyl.VinylExchange.cart.dto.CartDTO;
import com.vinyl.VinylExchange.cart.dto.CartItemDTO;
import com.vinyl.VinylExchange.cart.dto.UpdateCartItemRequest;
import com.vinyl.VinylExchange.listing.Listing;
import com.vinyl.VinylExchange.listing.ListingService;
import com.vinyl.VinylExchange.listing.enums.ListingStatus;
import com.vinyl.VinylExchange.shared.FileStorageService;
import com.vinyl.VinylExchange.shared.exception.CartItemNotFoundException;
import com.vinyl.VinylExchange.shared.exception.EmptyCartException;

import jakarta.transaction.Transactional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ListingService listingService;
    private final FileStorageService fileStorageService;

    public CartService(
            CartRepository cartRepository,
            ListingService listingService,
            CartItemRepository cartItemRepository,
            FileStorageService fileStorageService) {

        this.cartRepository = cartRepository;
        this.listingService = listingService;
        this.cartItemRepository = cartItemRepository;
        this.fileStorageService = fileStorageService;
    }

    @Transactional
    public Cart getOrCreateCart(UUID userId) {

        return cartRepository.findByUserId(userId).orElseGet(() -> {

            try {

                Cart newCart = new Cart(userId);

                return cartRepository.save(newCart);

            } catch (DataIntegrityViolationException e) {
                return cartRepository.findByUserId(userId)
                        .orElseThrow(() -> new RuntimeException("CartIssues, getOrCreate: " + e.getMessage()));
            }
        });

    }

    public Cart getCart(UUID userId) {

        return cartRepository
                .findByUserId(userId).orElseThrow(() -> new EmptyCartException());
    }

    public CartDTO getCartDTO(UUID userId) {

        Cart cart = getOrCreateCart(userId);

        Map<UUID, Listing> listingMap = getListingMapFromCart(cart);

        return buildCartDTO(cart, listingMap);
    }

    public CartDTO addToCart(UUID userId, UUID listingId, int quantity) {

        Cart cart = getOrCreateCart(userId);

        // service throws not found exception
        Listing listing = listingService.findListingById(listingId);

        Optional<CartItem> existingItem = cart.getCartItems()
                .stream()
                .filter(item -> item.getListingId().equals(listingId))
                .findFirst();

        if (existingItem.isPresent()) {

            CartItem item = existingItem.get();

            int newQuantity = item.getOrderQuantity() + quantity;

            if (!listing.hasEnoughStock(newQuantity)) {

                item.setOrderQuantity(listing.getStockQuantity());
            } else {

                item.setOrderQuantity(newQuantity);
            }

            cartItemRepository.save(item);

        } else {

            CartItem newItem = new CartItem(listingId, quantity);
            cart.addItem(newItem);
            cartItemRepository.save(newItem);
        }

        // fresh validation
        return getCartDTO(userId);

    }

    public CartDTO removeItemFromCart(UUID userId, UUID cartItemId) {

        Cart cart = getOrCreateCart(userId);

        CartItem cartItem = cart.getCartItems()
                .stream()
                .filter(item -> item.getCartItemId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new CartItemNotFoundException("Cart item not found"));

        cart.removeItem(cartItem);

        cartItemRepository.delete(cartItem);

        // fresh validation
        return getCartDTO(userId);
    }

    public void clearCart(UUID userId) {

        Cart cart = getOrCreateCart(userId);

        cartItemRepository.deleteAll(cart.getCartItems());

        cart.getCartItems().clear();

        cartRepository.save(cart);
    }

    public CartDTO updateCartItem(UUID userId, UUID cartItemId, UpdateCartItemRequest request) {

        Cart cart = getOrCreateCart(userId);

        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getCartItemId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new CartItemNotFoundException("Cart item not found"));

        Listing listing = listingService.findListingById(cartItem.getListingId());

        listing.hasEnoughStock(request.quantity());

        cartItem.setOrderQuantity(request.quantity());
        cartItemRepository.save(cartItem);

        return getCartDTO(userId);
    }

    public CartDTO decreaseItemQuantity(UUID userId, UUID listingId) {

        Cart cart = getOrCreateCart(userId);

        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getListingId().equals(listingId))
                .findFirst()
                .orElseThrow(() -> new CartItemNotFoundException("Cart item not found"));

        Listing listing = listingService.findListingById(listingId);

        int currentOrderQuantity = cartItem.getOrderQuantity();

        if (currentOrderQuantity == 1 || !listing.hasEnoughStock(currentOrderQuantity - 1)) {

            removeItemFromCart(userId, cartItem.getCartItemId());
        } else {
            cartItem.setOrderQuantity(cartItem.getOrderQuantity() - 1);
            cartItemRepository.save(cartItem);
        }

        return getCartDTO(userId);
    }

    public CartValidationResult validateAndFixCart(UUID userId) {

        Cart cart = getOrCreateCart(userId);

        Map<UUID, Listing> listingMap = getListingMapFromCart(cart);

        List<CartValidationIssue> issues = new ArrayList<>();

        List<CartItem> cartItemsToRemove = new ArrayList<>();

        for (CartItem cartItem : cart.getCartItems()) {

            Listing listing = listingService.findListingById(cartItem.getListingId());

            if (listing == null) {

                issues.add(CartValidationIssue.builder()
                        .cartItemId(cartItem.getCartItemId())
                        .listingId(cartItem.getListingId())
                        .type(CartValidationIssue.IssueType.LISTING_DELETED)
                        .message("This item is no longer on sale or removed entirely")
                        .build());

                cartItemsToRemove.add(cartItem);
                continue;
            }

            if (listing.getStockQuantity() == 0) {

                issues.add(CartValidationIssue.builder()
                        .cartItemId(cartItem.getCartItemId())
                        .listingId(cartItem.getListingId())
                        .type(CartValidationIssue.IssueType.LISTING_DELETED)
                        .message("This item is sold out")
                        .build());

                cartItemsToRemove.add(cartItem);
                continue;
            }

            if (!listing.isAvailable()) {

                String reason = listing.getStatus() == ListingStatus.SOLD ? "soldout" : "not available anymore";
                issues.add(CartValidationIssue.builder()
                        .cartItemId(cartItem.getCartItemId())
                        .listingId(cartItem.getListingId())
                        .type(CartValidationIssue.IssueType.LISTING_DELETED)
                        .message("This item is " + reason)
                        .build());

                cartItemsToRemove.add(cartItem);
                continue;
            }

            if (!listing.hasEnoughStock(cartItem.getOrderQuantity())) {

                issues.add(CartValidationIssue.builder()
                        .cartItemId(cartItem.getCartItemId())
                        .listingId(cartItem.getListingId())
                        .type(CartValidationIssue.IssueType.LISTING_DELETED)
                        .message("Stock is not enough to fulfill order, quantity adjusted")
                        .build());

                cartItem.setOrderQuantity(listing.getStockQuantity());
                continue;
            }
        }

        // ** delete unavailable items from cart/cartItemRepo
        for (CartItem item : cartItemsToRemove) {

            cart.removeItem(item);
            cartItemRepository.delete(item);
        }

        CartDTO cartDTO = buildCartDTO(cart, listingMap);

        cartDTO.setValidationIssues(issues);

        return CartValidationResult.builder()
                .cartDTO(cartDTO)
                .build();

    }

    private CartDTO buildCartDTO(Cart cart, Map<UUID, Listing> listingMap) {

        List<CartItemDTO> cartItemDTOs = new ArrayList<>();

        Long cartTotalDiscountedPrice = 0L;
        Long cartTotalPrice = 0L;

        for (CartItem cartItem : cart.getCartItems()) {
            Listing listing = listingMap.get(cartItem.getListingId());

            // remove? or check?
            if (listing == null) {
                continue;
            }

            CartItemDTO cartItemDTO = CartItemDTO.builder()
                    .id(cartItem.getCartItemId())
                    .listingId(listing.getId())
                    .title(listing.getTitle())
                    .artistName(listing.getArtistName())
                    .pricePerUnit(listing.getPriceKurus())
                    .orderQuantity(cartItem.getOrderQuantity())
                    .createdAt(cartItem.getCreatedAt())
                    .itemTotalPriceKurus(listing.getPriceKurus() * cartItem.getOrderQuantity())
                    .discountPerUnit(listing.getDiscountBP())
                    .discountedTotalPrice(listing.getDiscountedPriceKurus() * cartItem.getOrderQuantity())
                    .mainImagePath(fileStorageService.getMainImagePath(listing.getId()))
                    .build();

            cartItemDTOs.add(cartItemDTO);

            cartTotalDiscountedPrice += cartItemDTO.getDiscountedTotalPrice();
            cartTotalPrice += cartItemDTO.getItemTotalPriceKurus();

        }

        cartItemDTOs.sort(Comparator.comparing(CartItemDTO::getCreatedAt));

        return CartDTO.builder()
                .cartId(cart.getId())
                .items(cartItemDTOs)
                .totalPriceKurus(cartTotalPrice)
                .totalDiscountedPriceKurus(cartTotalDiscountedPrice)
                .totalItems(cartItemDTOs.size())
                .build();
    }

    private Map<UUID, Listing> getListingMapFromCart(Cart cart) {
        List<UUID> listingIdList = cart
                .getCartItems()
                .stream()
                .map(item -> item.getListingId())
                .toList();

        Map<UUID, Listing> listingMap = listingService.getListingsByIds(listingIdList)
                .stream()
                .collect(Collectors.toMap(listing -> listing.getId(), listing -> listing));

        return listingMap;
    }
}
