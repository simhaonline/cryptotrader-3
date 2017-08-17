package com.after_sunrise.cryptocurrency.cryptotrader.framework.impl;

import com.after_sunrise.cryptocurrency.cryptotrader.TestModule;
import com.after_sunrise.cryptocurrency.cryptotrader.core.PropertyManager;
import com.after_sunrise.cryptocurrency.cryptotrader.core.ServiceFactory;
import com.after_sunrise.cryptocurrency.cryptotrader.framework.Context;
import com.after_sunrise.cryptocurrency.cryptotrader.framework.Instruction;
import com.after_sunrise.cryptocurrency.cryptotrader.framework.OrderManager;
import com.after_sunrise.cryptocurrency.cryptotrader.framework.Trader.Request;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.after_sunrise.cryptocurrency.cryptotrader.framework.Trader.Request.ALL;
import static java.util.Collections.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * @author takanori.takase
 * @version 0.0.1
 */
public class AgentImplTest {

    private AgentImpl target;

    private TestModule module;

    private Context context;

    private PropertyManager propertyManager;

    private OrderManager service;

    @BeforeMethod
    public void setUp() {

        module = new TestModule();
        context = null;
        propertyManager = module.getMock(PropertyManager.class);
        service = module.getMock(OrderManager.class);

        Map<String, OrderManager> services = singletonMap("test", service);
        when(module.getMock(ServiceFactory.class).loadMap(OrderManager.class)).thenReturn(services);

        target = new AgentImpl(module.createInjector());

    }

    @Test
    public void testGet() throws Exception {

        assertEquals(target.get(), ALL);

    }

    @Test
    public void testManager() throws Exception {

        Request.RequestBuilder builder = module.createRequestBuilder();
        Request request = builder.build();
        List<Instruction> instructions = emptyList();
        Map<Instruction, String> results = new HashMap<>();
        when(service.manage(context, request, instructions)).thenReturn(results);

        // Dry
        when(propertyManager.getTradingActive()).thenReturn(false);
        assertNotSame(target.manage(context, request, instructions), results);
        verifyNoMoreInteractions(service);

        // Found
        when(propertyManager.getTradingActive()).thenReturn(true);
        assertSame(target.manage(context, request, instructions), results);
        verify(service).manage(context, request, instructions);

        // Site not found
        request = builder.site("hoge").build();
        assertNotSame(target.manage(context, request, instructions), results);
        verifyNoMoreInteractions(service);

        // Invalid request
        assertNotSame(target.manage(context, request, instructions), results);
        verifyNoMoreInteractions(service);

    }

    @Test
    public void testReconcile() throws Exception {

        Request.RequestBuilder builder = module.createRequestBuilder();
        Request request = builder.build();
        Map<Instruction, String> values = emptyMap();

        // Dry
        when(propertyManager.getTradingActive()).thenReturn(false);
        assertFalse(target.reconcile(context, request, values));
        verifyNoMoreInteractions(service);

        // Found
        when(propertyManager.getTradingActive()).thenReturn(true);
        assertFalse(target.reconcile(context, request, values));
        verify(service).reconcile(context, request, values);

        // Site not found
        request = builder.site("hoge").build();
        assertFalse(target.reconcile(context, request, values));
        verifyNoMoreInteractions(service);

        // Invalid request
        assertFalse(target.reconcile(context, request, values));
        verifyNoMoreInteractions(service);

    }

}