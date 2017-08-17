package com.after_sunrise.cryptocurrency.cryptotrader.framework.impl;

import com.after_sunrise.cryptocurrency.cryptotrader.TestModule;
import com.after_sunrise.cryptocurrency.cryptotrader.core.ServiceFactory;
import com.after_sunrise.cryptocurrency.cryptotrader.framework.Context;
import com.after_sunrise.cryptocurrency.cryptotrader.framework.Estimator;
import com.after_sunrise.cryptocurrency.cryptotrader.framework.Estimator.Estimation;
import com.after_sunrise.cryptocurrency.cryptotrader.framework.Trader.Request;
import org.mockito.Mockito;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Map;
import java.util.TreeMap;

import static java.math.BigDecimal.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

/**
 * @author takanori.takase
 * @version 0.0.1
 */
public class EstimatorImplTest {

    private static final BigDecimal HALF = new BigDecimal("0.5");

    private EstimatorImpl target;

    private TestModule module;

    private Map<String, Estimator> services;

    private Context context;

    private Request request;

    @BeforeMethod
    public void setUp() {

        services = new TreeMap<>();
        services.put("0", mock(Estimator.class));
        services.put("1", mock(Estimator.class));
        services.put("2", mock(Estimator.class));
        services.put("3", mock(Estimator.class));
        services.put("4", mock(Estimator.class));
        services.put("5", mock(Estimator.class));
        services.put("6", mock(Estimator.class));

        module = new TestModule();
        when(module.getMock(ServiceFactory.class).loadMap(Estimator.class)).thenReturn(services);

        context = module.getMock(Context.class);
        request = Request.builder().build();

        target = new EstimatorImpl(module.createInjector());

    }

    @Test
    public void testEstimate() throws Exception {

        // Valid Estimation (1st)
        Estimation estimation0 = Estimation.builder().price(TEN).confidence(HALF).build();
        when(services.get("0").estimate(context, request)).thenReturn(estimation0);

        // Null Estimation
        when(services.get("1").estimate(context, request)).thenReturn(null);

        // Null Price
        Estimation estimation2 = Estimation.builder().confidence(ONE).build();
        when(services.get("2").estimate(context, request)).thenReturn(estimation2);

        // Null Confidence
        Estimation estimation3 = Estimation.builder().price(ONE).build();
        when(services.get("3").estimate(context, request)).thenReturn(estimation3);

        // Execution Failure
        when(services.get("4").estimate(context, request)).thenThrow(new RuntimeException("test"));

        // Valid Estimation (2nd)
        Estimation estimation5 = Estimation.builder().price(ONE).confidence(ONE).build();
        when(services.get("5").estimate(context, request)).thenReturn(estimation5);

        // Valid Estimation (3rd)
        Estimation estimation6 = Estimation.builder().price(ONE).confidence(ZERO).build();
        when(services.get("6").estimate(context, request)).thenReturn(estimation6);

        // Consensus
        // Price = [(10 * 0.5) + (1 * 1) + (1 * 0)] / (0.5 + 1 + 0) = 6 / 1.5 = 4
        // Confidence = (0.5 + 1 + 0) / 3 = 0.5
        Estimation result = target.estimate(context, request);
        assertEquals(result.getPrice(), new BigDecimal("4.00000000"));
        assertEquals(result.getConfidence(), new BigDecimal("0.50000000"));
        services.values().forEach(mock -> Mockito.verify(mock).estimate(context, request));

    }

    @Test
    public void testEstimate_None() throws Exception {

        Estimation result = target.estimate(context, request);

        assertNull(result.getPrice());

        assertNull(result.getConfidence());

        services.values().forEach(mock -> Mockito.verify(mock).estimate(context, request));

    }

}