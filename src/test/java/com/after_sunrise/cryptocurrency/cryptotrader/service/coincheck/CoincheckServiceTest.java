package com.after_sunrise.cryptocurrency.cryptotrader.service.coincheck;

import com.after_sunrise.cryptocurrency.cryptotrader.framework.Context;
import com.after_sunrise.cryptocurrency.cryptotrader.framework.Context.Key;
import com.after_sunrise.cryptocurrency.cryptotrader.framework.Request;
import com.after_sunrise.cryptocurrency.cryptotrader.service.estimator.LastEstimator;
import com.after_sunrise.cryptocurrency.cryptotrader.service.estimator.MicroEstimator;
import com.after_sunrise.cryptocurrency.cryptotrader.service.estimator.MidEstimator;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Instant;

import static com.after_sunrise.cryptocurrency.cryptotrader.framework.Service.CurrencyType.BTC;
import static com.after_sunrise.cryptocurrency.cryptotrader.framework.Service.CurrencyType.JPY;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author takanori.takase
 * @version 0.0.1
 */
public class CoincheckServiceTest {

    private Context context;

    private Request request;

    private Key key;

    @BeforeMethod
    public void setUp() {

        context = mock(Context.class);

        request = Request.builder().site("s").instrument("i").currentTime(Instant.now()).build();
        when(context.getInstrumentCurrency(Key.from(request))).thenReturn(BTC);
        when(context.getFundingCurrency(Key.from(request))).thenReturn(JPY);
        when(context.findProduct(Key.builder().site("coincheck").instrument("*")
                .timestamp(request.getCurrentTime()).build(), BTC, JPY)).thenReturn("BTC_JPY");

        key = Key.builder().site("coincheck").instrument("BTC_JPY")
                .timestamp(request.getCurrentTime()).build();

    }

    @Test
    public void testCoincheckLastEstimator() {

        CoincheckService.CoincheckLastEstimator target = new CoincheckService.CoincheckLastEstimator();

        assertEquals(target.get(), "CoincheckLastEstimator");

        assertTrue(LastEstimator.class.isInstance(target));

        assertEquals(target.getKey(context, request), key);

    }

    @Test
    public void testCoincheckMicroEstimator() {

        CoincheckService.CoincheckMicroEstimator target = new CoincheckService.CoincheckMicroEstimator();

        assertEquals(target.get(), "CoincheckMicroEstimator");

        assertTrue(MicroEstimator.class.isInstance(target));

        assertEquals(target.getKey(context, request), key);

    }

    @Test
    public void testCoincheckMidEstimator() {

        CoincheckService.CoincheckMidEstimator target = new CoincheckService.CoincheckMidEstimator();

        assertEquals(target.get(), "CoincheckMidEstimator");

        assertTrue(MidEstimator.class.isInstance(target));

        assertEquals(target.getKey(context, request), key);

    }

}
