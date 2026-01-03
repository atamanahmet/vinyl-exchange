package com.vinyl.VinylExchange.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.vinyl.VinylExchange.domain.entity.Listing;
import com.vinyl.VinylExchange.domain.entity.Role;
import com.vinyl.VinylExchange.domain.entity.User;
import com.vinyl.VinylExchange.domain.enums.RoleName;
import com.vinyl.VinylExchange.service.AuthService;
import com.vinyl.VinylExchange.service.ListingService;
import com.vinyl.VinylExchange.service.RoleService;
import com.vinyl.VinylExchange.service.UserService;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RoleInitializer implements ApplicationRunner {

    private final RoleService roleService;
    private final AuthService authService;
    private final UserService userService;
    private final ListingService listingService;

    private static final Map<RoleName, String> ROLE_DESCRIPTIONS = Map.of(
            RoleName.ROLE_USER, "Regular user with basic permissions",
            RoleName.ROLE_ADMIN, "Administrator with full system access",
            RoleName.ROLE_MODERATOR, "Moderator with content management permissions");

    public RoleInitializer(
            RoleService roleService,
            AuthService authService,
            UserService userService,
            ListingService listingService) {

        this.roleService = roleService;
        this.authService = authService;
        this.userService = userService;
        this.listingService = listingService;

    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        log.info("Initializing roles...");

        ROLE_DESCRIPTIONS.forEach((roleName, description) -> {
            if (!roleService.existsByName(roleName)) {

                Role role = Role.builder()
                        .name(roleName)
                        .description(description)
                        .build();
                roleService.saveRole(role);

                log.info("Role created: " + roleName);
            } else {
                log.info("Role already exists: " + roleName);
            }
        });
        log.info("Role creation completed.");

        if (userService.existByUsername("admin")) {

            System.out.println("Givin roles = " + authService.giveUserAdminRole("admin"));
        } else {
            System.out.println("no admin");
        }

        User adminUser = userService.getByUsername("admin");

        if (adminUser != null) {
            List<Listing> mockList = generateMockListings(adminUser);

            for (Listing listing : mockList) {
                if (!listingService.isExistByTitle(listing.getTitle())) {
                    listingService.saveListing(listing);
                }
            }
        } else {
            log.info("admin is not present. create admin acc");
        }

    }

    public List<Listing> generateMockListings(User adminUser) {
        List<Listing> list = new ArrayList<>();

        list.addAll(List.of(
                Listing.builder()
                        .title("Abbey Road")
                        .artistName("The Beatles")
                        .labelName("Apple Records")
                        .format("LP")
                        .country("UK")
                        .date("1969")
                        .condition("VG")
                        .priceKurus(90000)
                        .owner(adminUser)
                        .build(),

                Listing.builder()
                        .title("Nevermind")
                        .artistName("Nirvana")
                        .labelName("DGC")
                        .format("LP")
                        .country("US")
                        .date("1991")
                        .condition("NM")
                        .priceKurus(78000)
                        .owner(adminUser)
                        .build(),

                Listing.builder()
                        .title("OK Computer")
                        .artistName("Radiohead")
                        .labelName("Parlophone")
                        .format("LP")
                        .country("UK")
                        .date("1997")
                        .condition("NM")
                        .priceKurus(82000)
                        .owner(adminUser)
                        .build(),

                Listing.builder()
                        .title("In Rainbows")
                        .artistName("Radiohead")
                        .labelName("XL Recordings")
                        .format("LP")
                        .country("EU")
                        .date("2007")
                        .condition("NM")
                        .priceKurus(76000)
                        .owner(adminUser)
                        .build(),

                Listing.builder()
                        .title("The Wall")
                        .artistName("Pink Floyd")
                        .labelName("Harvest")
                        .format("2xLP")
                        .country("UK")
                        .date("1979")
                        .condition("VG+")
                        .priceKurus(95000)
                        .owner(adminUser)
                        .build(),

                Listing.builder()
                        .title("Kind of Blue")
                        .artistName("Miles Davis")
                        .labelName("Columbia")
                        .format("LP")
                        .country("US")
                        .date("1959")
                        .condition("VG+")
                        .priceKurus(88000)
                        .owner(adminUser)
                        .build(),

                Listing.builder()
                        .title("Blue Train")
                        .artistName("John Coltrane")
                        .labelName("Blue Note")
                        .format("LP")
                        .country("US")
                        .date("1957")
                        .condition("VG")
                        .priceKurus(92000)
                        .owner(adminUser)
                        .build(),

                Listing.builder()
                        .title("Back to Black")
                        .artistName("Amy Winehouse")
                        .labelName("Island Records")
                        .format("LP")
                        .country("EU")
                        .date("2006")
                        .condition("NM")
                        .priceKurus(68000)
                        .owner(adminUser)
                        .build(),

                Listing.builder()
                        .title("Rumours")
                        .artistName("Fleetwood Mac")
                        .labelName("Warner Bros.")
                        .format("LP")
                        .country("US")
                        .date("1977")
                        .condition("VG+")
                        .priceKurus(74000)
                        .owner(adminUser)
                        .build(),

                Listing.builder()
                        .title("Led Zeppelin IV")
                        .artistName("Led Zeppelin")
                        .labelName("Atlantic")
                        .format("LP")
                        .country("UK")
                        .date("1971")
                        .condition("VG")
                        .priceKurus(86000)
                        .owner(adminUser)
                        .build(),

                Listing.builder()
                        .title("Master of Puppets")
                        .artistName("Metallica")
                        .labelName("Elektra")
                        .format("LP")
                        .country("US")
                        .date("1986")
                        .condition("NM")
                        .priceKurus(83000)
                        .owner(adminUser)
                        .build(),

                Listing.builder()
                        .title("Revolver")
                        .artistName("The Beatles")
                        .labelName("Parlophone")
                        .format("LP")
                        .country("UK")
                        .date("1966")
                        .condition("VG")
                        .priceKurus(89000)
                        .owner(adminUser)
                        .build(),

                Listing.builder()
                        .title("Unknown Pleasures")
                        .artistName("Joy Division")
                        .labelName("Factory")
                        .format("LP")
                        .country("UK")
                        .date("1979")
                        .condition("NM")
                        .priceKurus(81000)
                        .owner(adminUser)
                        .build(),

                Listing.builder()
                        .title("Disintegration")
                        .artistName("The Cure")
                        .labelName("Fiction")
                        .format("LP")
                        .country("UK")
                        .date("1989")
                        .condition("NM")
                        .priceKurus(79000)
                        .owner(adminUser)
                        .build(),

                Listing.builder()
                        .title("The Rise and Fall of Ziggy Stardust")
                        .artistName("David Bowie")
                        .labelName("RCA")
                        .format("LP")
                        .country("UK")
                        .date("1972")
                        .condition("VG+")
                        .priceKurus(84000)
                        .owner(adminUser)
                        .build(),

                Listing.builder()
                        .title("Discovery")
                        .artistName("Daft Punk")
                        .labelName("Virgin")
                        .format("LP")
                        .country("EU")
                        .date("2001")
                        .condition("NM")
                        .priceKurus(72000)
                        .owner(adminUser)
                        .build(),

                Listing.builder()
                        .title("To Pimp a Butterfly")
                        .artistName("Kendrick Lamar")
                        .labelName("Top Dawg")
                        .format("LP")
                        .country("US")
                        .date("2015")
                        .condition("NM")
                        .priceKurus(87000)
                        .owner(adminUser)
                        .build(),

                Listing.builder()
                        .title("Mezzanine")
                        .artistName("Massive Attack")
                        .labelName("Virgin")
                        .format("LP")
                        .country("UK")
                        .date("1998")
                        .condition("NM")
                        .priceKurus(80000)
                        .owner(adminUser)
                        .build(),

                Listing.builder()
                        .title("Violator")
                        .artistName("Depeche Mode")
                        .labelName("Mute")
                        .format("LP")
                        .country("UK")
                        .date("1990")
                        .condition("VG+")
                        .priceKurus(76000)
                        .owner(adminUser)
                        .build()));

        // System.out.println(list);
        return list;

    }
}
