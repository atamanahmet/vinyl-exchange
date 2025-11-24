package com.vinyl.VinylExchange.domain.pojo;

import java.util.List;

import lombok.Data;

@Data
public class RootResponse {
    private List<Release> releases;
}
