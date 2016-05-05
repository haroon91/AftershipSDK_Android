package com.aftershipsdk100.api.model;

import java.util.Date;
import java.util.List;

/**
 * Created by Haroon on 3/5/2016.
 */
public class Tracking {

    public String id;
    public Date createdAt;
    public Date updatedAt;
    public String tracking_number;
    public String trackingPostalCode;
    public String trackingShipDate;
    public String trackingAccountNo;
    public String trackingKey;
    public String trackingDestinationCountry;

    public String slug;
    public boolean active;
    public List<String> android;
    public CustomFields custom_fields;
    public String customerName;
    public int deliveryTime;
    public String destinationCountryIso3;
    public List<String> emails;
    public String expectedDelivery;

    public String order_id;
    public String order_id_path;
    public String originCountryIso3;
    public String uniqueToken;
    public int shipmentPackageCount;
    public String shipmentType;
    public int shipmentWeight;
    public String shipmentWeightUnit;
    public String signedBy;
    public List<String> smses;
    public String title;
    public String tag;
    public int trackedCount;
    public List<Checkpoint> checkpoints;



}
