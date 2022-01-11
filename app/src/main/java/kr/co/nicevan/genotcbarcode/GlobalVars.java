package kr.co.nicevan.genotcbarcode;

/**
 * Created by david on 2020-04-29
 * Copyright (c) GINU Co., Ltd. All rights reserved.
 */


/**
 * 사용예:
 * 1. catId 가져오기
 * String catId = GlobalVars.getCatId();
 * 2. catId 저장하기
 * GlobalVars.setCatId(catId);
 */
public class GlobalVars {
	public static String getCardId()
	{
		return mCardId;
	}
	public static void setCardId(String cardId)
	{
		mCardId = cardId;
	}
	public static String getSessionKey()
	{
		return mSessionKey;
	}
	public static void setSessionKey(String sessionKey)
	{
		mSessionKey = sessionKey;
	}
	public static String getTransId()
	{
		return mTransId;
	}
	public static void setTransId(String transId)
	{
		mTransId = transId;
	}
	private static String mCardId;
	private static String mSessionKey;
	private static String mTransId;
}
