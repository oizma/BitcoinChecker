package com.mobnetic.coinguardian.model.market;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mobnetic.coinguardian.model.CheckerInfo;
import com.mobnetic.coinguardian.model.CurrencyPairInfo;
import com.mobnetic.coinguardian.model.Market;
import com.mobnetic.coinguardian.model.Ticker;
import com.mobnetic.coinguardiandatamodule.R;

public class AllCoin extends Market {

	private final static String NAME = "AllCoin";
	private final static String TTS_NAME = "All Coin";
	private final static String URL = "https://www.allcoin.com/api2/pair/%1$s_%2$s";
	private final static String URL_CURRENCY_PAIRS = "https://www.allcoin.com/api2/pairs";
	private final static HashMap<String, CharSequence[]> CURRENCY_PAIRS = new LinkedHashMap<String, CharSequence[]>();
	
	public AllCoin() {
		super(NAME, TTS_NAME, CURRENCY_PAIRS);
	}

	@Override
	public String getUrl(int requestId, CheckerInfo checkerInfo) {
		return String.format(URL, checkerInfo.getCurrencyBase(), checkerInfo.getCurrencyCounter());
	}
	
	@Override
	protected void parseTickerFromJsonObject(int requestId, JSONObject jsonObject, Ticker ticker, CheckerInfo checkerInfo) throws Exception {
		final JSONObject dataJsonObject = jsonObject.getJSONObject("data");
		ticker.bid = getDoubleFromString(dataJsonObject, "top_bid");
		ticker.ask = getDoubleFromString(dataJsonObject, "top_ask");
		ticker.vol = getDoubleFromString(dataJsonObject, "volume_24h_"+checkerInfo.getCurrencyBase());
		ticker.high = getDoubleFromString(dataJsonObject, "max_24h_price");
		ticker.low = getDoubleFromString(dataJsonObject, "min_24h_price");
		ticker.last = getDoubleFromString(dataJsonObject, "trade_price");
	}
	
	private double getDoubleFromString(JSONObject jsonObject, String name) throws NumberFormatException, JSONException {
		return Double.parseDouble(jsonObject.getString(name));
	}
	
	@Override
	protected String parseErrorFromJsonObject(int requestId, JSONObject jsonObject, CheckerInfo checkerInfo) throws Exception {
		return jsonObject.getString("error_info");
	}
	
	@Override
	public int getCautionResId() {
		return R.string.market_caution_allcoin;
	}
	
	// ====================
	// Get currency pairs
	// ====================
	@Override
	public String getCurrencyPairsUrl(int requestId) {
		return URL_CURRENCY_PAIRS;
	}
	
	@Override
	protected void parseCurrencyPairsFromJsonObject(int requestId, JSONObject jsonObject, List<CurrencyPairInfo> pairs) throws Exception {
		final JSONObject dataJsonObject = jsonObject.getJSONObject("data");
		
		final JSONArray pairsJsonArray = dataJsonObject.names();
		for(int i=0; i<pairsJsonArray.length(); ++i) {
			final String pairName = pairsJsonArray.getString(i);
			final JSONObject marketJsonObject = dataJsonObject.getJSONObject(pairName);
			pairs.add(new CurrencyPairInfo(
					marketJsonObject.getString("type"),
					marketJsonObject.getString("exchange"),
					pairName));
		}
	}
}