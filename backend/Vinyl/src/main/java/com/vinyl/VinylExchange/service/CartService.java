package com.vinyl.VinylExchange.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.vinyl.VinylExchange.domain.dto.CartDTO;
import com.vinyl.VinylExchange.domain.dto.CartItemDTO;
import com.vinyl.VinylExchange.domain.dto.CartValidationIssue;
import com.vinyl.VinylExchange.domain.dto.UpdateCartItemRequest;
import com.vinyl.VinylExchange.domain.entity.Cart;
import com.vinyl.VinylExchange.domain.entity.CartItem;
import com.vinyl.VinylExchange.domain.entity.CartValidationResult;
import com.vinyl.VinylExchange.domain.entity.Listing;
import com.vinyl.VinylExchange.domain.entity.ListingStatus;
import com.vinyl.VinylExchange.exception.CartItemNotFoundException;
import com.vinyl.VinylExchange.repository.CartItemRepository;
import com.vinyl.VinylExchange.repository.CartRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ListingService listingService;

    public CartService(
            CartRepository cartRepository,
            ListingService listingService,
            CartItemRepository cartItemRepository) {

        this.cartRepository = cartRepository;
        this.listingService = listingService;
        this.cartItemRepository = cartItemRepository;
    }

    private Cart getOrCreateCart(UUID userId) {

        return cartRepository
                .findByUser_Id(userId)
                .orElseGet(() -> {

                    Cart newCart = new Cart(userId);

                    return cartRepository.save(newCart);
                });
    }

    public CartDTO getCartDTO(UUID userId) {

        Cart cart = getOrCreateCart(userId);

        Map<UUID, Listing> listingMap = getListingMapFromCart(cart);

        return buildCartDTO(cart, listingMap);
    }

    public CartDTO addToCart(UUID userId, UUID listingId, int quantity) {

        Cart cart = getOrCreateCart(userId);

        // service throws not found exception
        Listing listing = listingService.getListingById(listingId);

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

        Listing listing = listingService.getListingById(cartItem.getListingId());

        listing.hasEnoughStock(request.quantity());

        cartItem.setOrderQuantity(request.quantity());
        cartItemRepository.save(cartItem);

        return getCartDTO(userId);
    }

    public CartValidationResult validateAndFixCart(UUID userId) {

        Cart cart = getOrCreateCart(userId);

        Map<UUID, Listing> listingMap = getListingMapFromCart(cart);

        List<CartValidationIssue> issues = new ArrayList<>();

        List<CartItem> cartItemsToRemove = new ArrayList<>();

        for (CartItem cartItem : cart.getCartItems()) {

            Listing listing = listingService.getListingById(cartItem.getListingId());

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
                    .pricePerUnit(listing.getPriceKurus())
                    .itemTotalPriceKurus(listing.getPriceKurus() * cartItem.getOrderQuantity())
                    .discountPerUnit(listing.getDiscountBP())
                    .discountedTotalPrice(listing.getDiscountedPriceKurus() * cartItem.getOrderQuantity())
                    .mainImagePath(!listing.getImagePaths().isEmpty() ? listing.getImagePaths().get(0) : null)
                    .build();

            cartItemDTOs.add(cartItemDTO);

            cartTotalPrice += cartItemDTO.getDiscountedTotalPrice();

        }

        return CartDTO.builder()
                .items(cartItemDTOs)
                .totalPriceKurus(cartTotalPrice)
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

    // List<UUID> listingIds = cart.getCartItems()
    // .stream()
    // .map(item -> item.getListingId())
    // .collect(Collectors.toList());

    // System.out.println(listingId.toString());

    // public CartDTO getCartDTO(User user) {

    // Cart cart = cartRepository
    // .findByUser(user)
    // .orElseGet(() -> {

    // Cart newCart = new Cart();

    // newCart.setUser(user);

    // return cartRepository.save(newCart);
    // });
    // return new CartDTO(cart);
    // }

    // public Cart getOrCreateCart(User user) {

    // return cartRepository
    // .findByUser(user)
    // .orElseGet(() -> {

    // Cart newCart = new Cart();

    // newCart.setUser(user);
    // user.setCart(newCart);

    // return cartRepository.save(newCart);
    // });
    // }

    // public Cart addToCart(User user, UUID listingId) {

    // Listing listing = listingService.getListingById(listingId);

    // if (listing.getQuantity() < 1) {
    // throw new InsufficientStockException();
    // }

    // Cart cart = getOrCreateCart(user);

    // CartItem cartItem = cart.getCartItems()
    // .stream()
    // .filter(item -> item.getListingId().equals(listingId)).findFirst()
    // .orElse(null);

    // if (cartItem == null) {
    // cartItem = new CartItem(cart, listing);
    // cart.getCartItems().add(cartItem);

    // } else {

    // if (cartItem.getQuantity() + 1 > listing.getQuantity()) {
    // throw new InsufficientStockException();
    // }

    // cartItem.setQuantity(cartItem.getQuantity() + 1);
    // }

    // return cartRepository.save(cart);
    // }

    // public void removeFromCart(User user, UUID cartItemId) {

    // Cart cart = getOrCreateCart(user);

    // if (cart.getCartItems().isEmpty()) {
    // throw new CartItemNotFoundException("Cart is empty");
    // }

    // CartItem cartItem = cart
    // .getCartItems()
    // .stream()
    // .filter(item -> item.getId().equals(cartItemId))
    // .findFirst()
    // .orElseThrow(() -> new CartItemNotFoundException());

    // cart.getCartItems().remove(cartItem);

    // cartRepository.save(cart);
    // }

    // public void decreaseItemQuantity(User user, UUID cartItemId) {

    // Cart cart = getOrCreateCart(user);

    // if (cart.getCartItems().isEmpty()) {
    // throw new CartItemNotFoundException("Cart is already empty");
    // }

    // CartItem cartItem = cart
    // .getCartItems()
    // .stream()
    // .filter(item -> item.getId().equals(cartItemId))
    // .findFirst()
    // .orElseThrow(() -> new CartItemNotFoundException());

    // if (cartItem.getQuantity() == 1) {

    // cart.getCartItems().remove(cartItem);
    // } else {

    // cartItem.setQuantity(cartItem.getQuantity() - 1);
    // }

    // cartRepository.save(cart);
    // }

    // public List<CartItemDTO> getCartItems(User user) {

    // Cart cart = getOrCreateCart(user);

    // List<CartItemDTO> cartItemDTOs = new ArrayList<>();

    // cart.getCartItems()
    // .forEach(item -> cartItemDTOs.add(new CartItemDTO(item)));

    // return cartItemDTOs;
    // }

    // public List<CartListingDTO> getCartListings(User user) {
    // Cart cart = getOrCreateCart(user);
    // List<CartListingDTO> cartListingDTOs = new ArrayList<>();

    // cart.getCartItems().forEach(item-> cartListingDTOs.add(new
    // CartListingDTO(item.,listingService.getListingById(item.getListingId()))));
    // }
    // public List<Listing> getCartListings(User user) {

    // Cart cart = getOrCreateCart(user);

    // List<CartItemDTO> cartItemDTOs = new ArrayList<>();

    // cart.getItems()
    // .forEach(item -> cartItemDTOs.add(new CartItemDTO(item)));

    // return cartItemDTOs;
    // }
}
