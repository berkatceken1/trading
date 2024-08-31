package com.berk.controller;

import com.berk.model.Coin;
import com.berk.service.CoinService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/coins")
public class CoinController {

    @Autowired
    private CoinService coinService;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping
    ResponseEntity<List<Coin>>getCoinsList(@RequestParam("page") int page) throws Exception {
        List<Coin> coinList = coinService.getCoinsList(page);
        return new ResponseEntity<>(coinList, HttpStatus.ACCEPTED);
    }

    @GetMapping("/{coinId}/market_chart")
    ResponseEntity<JsonNode>getMarketChart(
            @PathVariable String coinId,
            @RequestParam("days") int days)
            throws Exception {
        String marketChart = coinService.getMarketChart(coinId, days);
        JsonNode jsonNode = objectMapper.readTree(marketChart);
        return new ResponseEntity<>(jsonNode, HttpStatus.ACCEPTED);
    }

    @GetMapping("/search")
    ResponseEntity<JsonNode> searchCoin(@RequestParam("query") String query) throws Exception {
        String searchCoin = coinService.searchCoin(query);
        JsonNode jsonNode = objectMapper.readTree(searchCoin);
        return ResponseEntity.ok(jsonNode);
    }

    @GetMapping("/top50")
    ResponseEntity<JsonNode> getTop50CoinsByMarketCap() throws Exception {
        String top50CoinsByMarketCap = coinService.getTop50CoinsByMarketCap();
        JsonNode jsonNode = objectMapper.readTree(top50CoinsByMarketCap);
        return ResponseEntity.ok(jsonNode);
    }

    @GetMapping("/trading")
    ResponseEntity<JsonNode> getTradingCoins() throws Exception {
        String tradingCoins = coinService.getTradingCoins();
        JsonNode jsonNode = objectMapper.readTree(tradingCoins);
        return ResponseEntity.ok(jsonNode);
    }

    @GetMapping("details/{coinId}")
    ResponseEntity<JsonNode> getCoinInfo(@PathVariable String coinId) throws Exception {
        String coinInfo = coinService.getCoinInfo(coinId);
        JsonNode jsonNode = objectMapper.readTree(coinInfo);
        return ResponseEntity.ok(jsonNode);
    }


}
