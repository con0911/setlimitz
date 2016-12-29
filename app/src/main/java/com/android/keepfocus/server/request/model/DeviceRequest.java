package com.android.keepfocus.server.request.model;

import com.android.keepfocus.server.model.Device;

/**
 * Created by sev_user on 9/23/2016.
 */
public class DeviceRequest {
    private int Type;
    private int Action;
    private Device Device;
    private String OwnerRegistationId;


    public DeviceRequest(int type, int action, String ownerRegistationId, Device device) {
        Action = action;
        Type = type;
        this.Device = device;
        this.OwnerRegistationId = ownerRegistationId;
    }

    public DeviceRequest(int type, int action, Device device) {
        Action = action;
        Type = type;
        this.Device = device;
    }
}
