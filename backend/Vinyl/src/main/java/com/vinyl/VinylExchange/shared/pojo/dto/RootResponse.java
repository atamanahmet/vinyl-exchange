package com.vinyl.VinylExchange.shared.pojo.dto;

import java.util.List;

import com.vinyl.VinylExchange.shared.pojo.Release;

import lombok.Data;

@Data
public class RootResponse {

    private List<Release> releases;
}
