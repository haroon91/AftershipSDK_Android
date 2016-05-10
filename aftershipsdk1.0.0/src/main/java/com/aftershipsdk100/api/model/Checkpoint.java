package com.aftershipsdk100.api.model;

import java.util.Date;

/**
 * Data structure for information about checkpoints of a tracking
 */
public class Checkpoint {

    public Date createdAt;
    public String slug;
    public String checkpointTime;
    public String city;

    public String countryIso3;
    public String countryName;
    public String message;
    public String state;
    public String tag;
    public String zip;
}
