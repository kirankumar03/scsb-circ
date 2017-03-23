package org.recap.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCase;
import org.recap.gfa.model.*;
import org.recap.ils.model.response.ItemInformationResponse;
import org.recap.model.ItemRequestInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.sql.Time;
import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 21/2/17.
 */
public class GFAServiceUT extends BaseTestCase{

    private static final Logger logger = LoggerFactory.getLogger(GFAServiceUT.class);

    @Mock
    GFAService gfaService;

    @Autowired
    GFAService getGfaService;

    @Mock
    RestTemplate restTemplate;

    @Value("${gfa.item.status}")
    private String gfaItemStatus;

    @Value("${gfa.item.retrieval.order}")
    private String gfaItemRetrival;

    @Value("${gfa.item.permanent.withdrawl.direct}")
    private String gfaItemPermanentWithdrawlDirect;

    @Value("${gfa.item.permanent.withdrawl.indirect}")
    private String gfaItemPermanentWithdrawlInDirect;

    @Test
    public void testGFAService(){
        GFARetrieveItemRequest gfaRetrieveItemRequest = new GFARetrieveItemRequest();
        RetrieveItemRequest retrieveItemRequest = new RetrieveItemRequest();
        TtitemRequest ttitemRequest = new TtitemRequest();
        ttitemRequest.setCustomerCode("PB");
        ttitemRequest.setDestination("PUL");
        ttitemRequest.setItemBarcode("123");
        ttitemRequest.setItemStatus("Available");
        retrieveItemRequest.setTtitem(Arrays.asList(ttitemRequest));
        gfaRetrieveItemRequest.setRetrieveItem(retrieveItemRequest);


        RetrieveItem retrieveItem = null;
        GFARetrieveItemResponse gfaRetrieveItemResponse = new GFARetrieveItemResponse();
        gfaRetrieveItemResponse.setSuccess(true);
        gfaRetrieveItemResponse.setRetrieveItem(retrieveItem);
        gfaRetrieveItemResponse.setScrenMessage("Success");

        ResponseEntity<GFARetrieveItemResponse> responseEntity = new ResponseEntity(gfaRetrieveItemResponse, HttpStatus.OK);
        HttpEntity requestEntity = new HttpEntity(gfaRetrieveItemRequest, getHttpHeaders());
        Mockito.when(gfaService.getGfaItemRetrival()).thenReturn(gfaItemRetrival);
        Mockito.when(gfaService.getGfaItemStatus()).thenReturn(gfaItemStatus);
        Mockito.when(gfaService.getRestTemplate()).thenReturn(restTemplate);
        Mockito.when(gfaService.getRestTemplate().exchange(gfaService.getGfaItemRetrival(), HttpMethod.POST, requestEntity, GFARetrieveItemResponse.class)).thenReturn(responseEntity);
        Mockito.when(gfaService.itemRetrival(gfaRetrieveItemRequest)).thenCallRealMethod();
        GFARetrieveItemResponse response = gfaService.itemRetrival(gfaRetrieveItemRequest);
        assertNotNull(response);
        assertNull(response.getRetrieveItem());
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @Test
    public void testGfaPWD() {
        GFAPwdRequest gfaPwdRequest = new GFAPwdRequest();
        GFAPwdDsItemRequest gfaPwdDsItemRequest = new GFAPwdDsItemRequest();
        GFAPwdTtItemRequest gfaPwdTtItemRequest = new GFAPwdTtItemRequest();
        gfaPwdTtItemRequest.setCustomerCode("AR");
        gfaPwdTtItemRequest.setItemBarcode("AR00000612");
        gfaPwdTtItemRequest.setDestination("AR");
        gfaPwdTtItemRequest.setRequestor("Dev Tesr");
        gfaPwdDsItemRequest.setTtitem(Arrays.asList(gfaPwdTtItemRequest));
        gfaPwdRequest.setDsitem(gfaPwdDsItemRequest);

        GFAPwdResponse gfaPwdResponse = new GFAPwdResponse();
        GFAPwdDsItemResponse gfaPwdDsItemResponse = getGFAPwdDsItemResponse();
        assertNotNull(gfaPwdDsItemResponse.getProdsBefore());
        assertNotNull(gfaPwdDsItemResponse.getTtitem());
        assertNotNull(gfaPwdDsItemResponse.getProdsHasChanges());
        gfaPwdResponse.setDsitem(gfaPwdDsItemResponse);

        ResponseEntity<GFAPwdResponse> responseEntity = new ResponseEntity(gfaPwdResponse, HttpStatus.OK);
        HttpEntity requestEntity = new HttpEntity(gfaPwdRequest, getHttpHeaders());
        Mockito.when(gfaService.getGfaItemPermanentWithdrawlDirect()).thenReturn(gfaItemPermanentWithdrawlDirect);
        Mockito.when(gfaService.getRestTemplate()).thenReturn(restTemplate);
        Mockito.when(gfaService.getRestTemplate().exchange(gfaService.getGfaItemPermanentWithdrawlDirect(), HttpMethod.POST, requestEntity, GFAPwdResponse.class)).thenReturn(responseEntity);
        Mockito.when(gfaService.gfaPermanentWithdrawlDirect(gfaPwdRequest)).thenCallRealMethod();
        GFAPwdResponse response = gfaService.gfaPermanentWithdrawlDirect(gfaPwdRequest);
        assertNotNull(response);
        assertNotNull(response.getDsitem());
    }

    @Test
    public void testcallItemEDDRetrivate(){
        ItemRequestInformation itemRequestInformation = getItemRequestInformation();
        ItemInformationResponse itemInformationResponse = getItemInformationResponse();
        ItemInformationResponse response = getGfaService.callItemEDDRetrivate(itemRequestInformation,itemInformationResponse);
        assertNotNull(response);
        assertEquals(response.getScreenMessage(),"Retrieval order successfully created in GFA.");
    }

    @Test
    public void testGfaPWI() {
        GFAPwiRequest gfaPwiRequest = new GFAPwiRequest();
        GFAPwiDsItemRequest gfaPwiDsItemRequest = new GFAPwiDsItemRequest();
        GFAPwiTtItemRequest gfaPwiTtItemRequest = new GFAPwiTtItemRequest();
        gfaPwiTtItemRequest.setCustomerCode("AR");
        gfaPwiTtItemRequest.setItemBarcode("AR00051608");
        gfaPwiDsItemRequest.setTtitem(Arrays.asList(gfaPwiTtItemRequest));
        gfaPwiRequest.setDsitem(gfaPwiDsItemRequest);

        GFAPwiResponse gfaPwiResponse = new GFAPwiResponse();
        GFAPwiDsItemResponse gfaPwiDsItemResponse = getGFAPwiDsItemResponse();

        assertNotNull(gfaPwiDsItemResponse.getProdsBefore());
        assertNotNull(gfaPwiDsItemResponse.getProdsHasChanges());
        assertNotNull(gfaPwiDsItemResponse.getTtitem());
        gfaPwiResponse.setDsitem(gfaPwiDsItemResponse);

        ResponseEntity<GFAPwiResponse> responseEntity = new ResponseEntity(gfaPwiResponse, HttpStatus.OK);
        HttpEntity requestEntity = new HttpEntity(gfaPwiRequest, getHttpHeaders());
        Mockito.when(gfaService.getGfaItemPermanentWithdrawlInDirect()).thenReturn(gfaItemPermanentWithdrawlInDirect);
        Mockito.when(gfaService.getRestTemplate()).thenReturn(restTemplate);
        Mockito.when(gfaService.getRestTemplate().exchange(gfaService.getGfaItemPermanentWithdrawlInDirect(), HttpMethod.POST, requestEntity, GFAPwiResponse.class)).thenReturn(responseEntity);
        Mockito.when(gfaService.gfaPermanentWithdrawlInDirect(gfaPwiRequest)).thenCallRealMethod();
        GFAPwiResponse response = gfaService.gfaPermanentWithdrawlInDirect(gfaPwiRequest);
        assertNotNull(response);
        assertNotNull(response.getDsitem());
    }

    @Test
    public void testGFAPwdTtItemResponse(){
        GFAPwdTtItemResponse gfaPwdTtItemResponse = getGFAPwdTtItemResponse();
        assertNotNull(gfaPwdTtItemResponse.getCustomerCode());
        assertNotNull(gfaPwdTtItemResponse.getItemBarcode());
        assertNotNull(gfaPwdTtItemResponse.getDestination());
        assertNotNull(gfaPwdTtItemResponse.getDeliveryMethod());
        assertNotNull(gfaPwdTtItemResponse.getRequestor());
        assertNotNull(gfaPwdTtItemResponse.getRequestorFirstName());
        assertNotNull(gfaPwdTtItemResponse.getRequestorLastName());
        assertNotNull(gfaPwdTtItemResponse.getRequestorMiddleName());
        assertNotNull(gfaPwdTtItemResponse.getRequestorEmail());
        assertNotNull(gfaPwdTtItemResponse.getRequestorOther());
        assertNotNull(gfaPwdTtItemResponse.getPriority());
        assertNotNull(gfaPwdTtItemResponse.getNotes());
        assertNotNull(gfaPwdTtItemResponse.getRequestDate());
        assertNotNull(gfaPwdTtItemResponse.getRequestTime());
        assertNotNull(gfaPwdTtItemResponse.getErrorCode());
        assertNotNull(gfaPwdTtItemResponse.getErrorNote());
    }


    public GFAPwdTtItemResponse getGFAPwdTtItemResponse(){
        GFAPwdTtItemResponse gfaPwdTtItemResponse = new GFAPwdTtItemResponse();
        gfaPwdTtItemResponse.setCustomerCode("PB");
        gfaPwdTtItemResponse.setItemBarcode("231365");
        gfaPwdTtItemResponse.setDestination("test");
        gfaPwdTtItemResponse.setDeliveryMethod("test");
        gfaPwdTtItemResponse.setRequestor("test");
        gfaPwdTtItemResponse.setRequestorFirstName("test");
        gfaPwdTtItemResponse.setRequestorLastName("test");
        gfaPwdTtItemResponse.setRequestorMiddleName("test");
        gfaPwdTtItemResponse.setRequestorEmail("hemalatha.s@htcindia.com");
        gfaPwdTtItemResponse.setRequestorOther("test");
        gfaPwdTtItemResponse.setPriority("first");
        gfaPwdTtItemResponse.setNotes("test");
        gfaPwdTtItemResponse.setRequestDate(new Date());
        gfaPwdTtItemResponse.setRequestTime(new Time(new Long(10)));
        gfaPwdTtItemResponse.setErrorCode("test");
        gfaPwdTtItemResponse.setErrorNote("test");
        return gfaPwdTtItemResponse;
    }


    public GFAPwdDsItemResponse getGFAPwdDsItemResponse(){
        GFAPwdTtItemResponse gfaPwdTtItemResponse = getGFAPwdTtItemResponse();
        GFAPwdDsItemResponse gfaPwdDsItemResponse = new GFAPwdDsItemResponse();
        gfaPwdDsItemResponse.setTtitem(Arrays.asList(gfaPwdTtItemResponse));
        gfaPwdDsItemResponse.setProdsBefore(new ProdsBefore());
        gfaPwdDsItemResponse.setProdsHasChanges(true);
        return gfaPwdDsItemResponse;
    }

    @Test
    public void testGFAPwiTtItemResponse(){
        GFAPwiTtItemResponse gfaPwiTtItemResponse = getGFAPwiTtItemResponse();
        assertNotNull(gfaPwiTtItemResponse.getCustomerCode());
        assertNotNull(gfaPwiTtItemResponse.getErrorCode());
        assertNotNull(gfaPwiTtItemResponse.getErrorNote());
        assertNotNull(gfaPwiTtItemResponse.getItemBarcode());
    }

    public GFAPwiTtItemResponse getGFAPwiTtItemResponse(){
        GFAPwiTtItemResponse gfaPwiTtItemResponse = new GFAPwiTtItemResponse();
        gfaPwiTtItemResponse.setCustomerCode("PB");
        gfaPwiTtItemResponse.setErrorCode("test");
        gfaPwiTtItemResponse.setErrorNote("test");
        gfaPwiTtItemResponse.setItemBarcode("336985245642355");
        return gfaPwiTtItemResponse;
    }

    public GFAPwiDsItemResponse getGFAPwiDsItemResponse(){
        GFAPwiDsItemResponse gfaPwiDsItemResponse = new GFAPwiDsItemResponse();
        gfaPwiDsItemResponse.setTtitem(Arrays.asList(getGFAPwiTtItemResponse()));
        gfaPwiDsItemResponse.setProdsBefore(new ProdsBefore());
        gfaPwiDsItemResponse.setProdsHasChanges(true);
        return gfaPwiDsItemResponse;
    }

    public ItemRequestInformation getItemRequestInformation(){
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setItemBarcodes(Arrays.asList("123"));
        itemRequestInformation.setPatronBarcode("45678915");
        itemRequestInformation.setUsername("Discovery");
        itemRequestInformation.setBibId("12");
        itemRequestInformation.setItemOwningInstitution("PUL");
        itemRequestInformation.setCallNumber("X");
        itemRequestInformation.setAuthor("John");
        itemRequestInformation.setTitleIdentifier("test");
        itemRequestInformation.setTrackingId("235");
        itemRequestInformation.setRequestingInstitution("PUL");
        itemRequestInformation.setDeliveryLocation("PB");
        itemRequestInformation.setExpirationDate("30-03-2017 00:00:00");
        itemRequestInformation.setCustomerCode("PB");
        itemRequestInformation.setRequestNotes("test");
        itemRequestInformation.setRequestType("RETRIEVAL");
        itemRequestInformation.setChapterTitle("test");
        itemRequestInformation.setVolume("5");
        itemRequestInformation.setIssue("test");
        itemRequestInformation.setEmailAddress("hemalatha.s@htcindia.com");
        itemRequestInformation.setStartPage("10");
        itemRequestInformation.setEndPage("100");

        return itemRequestInformation;
    }

    public ItemInformationResponse getItemInformationResponse(){
        ItemInformationResponse itemInformationResponse = new ItemInformationResponse();
        itemInformationResponse.setCirculationStatus("test");
        itemInformationResponse.setSecurityMarker("test");
        itemInformationResponse.setFeeType("test");
        itemInformationResponse.setTransactionDate(new Date().toString());
        itemInformationResponse.setHoldQueueLength("10");
        itemInformationResponse.setTitleIdentifier("test");
        itemInformationResponse.setBibID("1223");
        itemInformationResponse.setDueDate(new Date().toString());
        itemInformationResponse.setExpirationDate("30-03-2017 00:00:00");
        itemInformationResponse.setRecallDate(new Date().toString());
        itemInformationResponse.setCurrentLocation("test");
        itemInformationResponse.setHoldPickupDate(new Date().toString());
        itemInformationResponse.setItemBarcode("32101077423406");
        itemInformationResponse.setRequestType("RECALL");
        itemInformationResponse.setRequestingInstitution("CUL");
        itemInformationResponse.setRequestId(392);
        return itemInformationResponse;
    }

}