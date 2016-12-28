package com.android.keepfocus.gcm;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.keepfocus.R;
import com.android.keepfocus.activity.ChildSchedulerActivity;
import com.android.keepfocus.activity.DeviceMemberManagerment;
import com.android.keepfocus.activity.FamilyManagerment;
import com.android.keepfocus.activity.JoinGroupActivity;
import com.android.keepfocus.activity.SetupWizardActivity;
import com.android.keepfocus.data.ChildKeepFocusItem;
import com.android.keepfocus.data.ChildTimeItem;
import com.android.keepfocus.data.MainDatabaseHelper;
import com.android.keepfocus.data.ParentGroupItem;
import com.android.keepfocus.data.ParentMemberItem;
import com.android.keepfocus.data.ParentProfileItem;
import com.android.keepfocus.data.ParentTimeItem;
import com.android.keepfocus.server.request.controllers.GroupRequestController;
import com.android.keepfocus.server.request.controllers.NotificationController;
import com.android.keepfocus.utils.Constants;
import com.android.keepfocus.utils.MainUtils;
import com.google.android.gms.gcm.GcmListenerService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by sev_user on 9/20/2016.
 */
public class MyGcmPushReceiver extends GcmListenerService {

    public static final String DELETE_NOTI = "del";
    public static final String CREATE_NOTI = "create";
    public static final String UPDATE_NOTI = "update";
    public static final String JOIN_GROUP = "join";
    public static final String MANAGER_JOIN_GROUP = "managerjoin";
    public static final String ACCEPT_MANAGER_JOIN = "acceptjoin";
    public static final String REJECT_MANAGER_JOIN = "rejectjoin";
    public static final String MANAGER = "manager";
    public static final String CHILDREN = "child";
    public static final String REPLACE_DEVICE = "replace";
    public static final String BLOCKALL = "blockall";
    public static final String UNBLOCKALL = "unblockall";
    public static final String ALLOWALL = "allowall";
    public static final String UNALLOWALL = "unallowall";
    public static final String BLOCK_SETTINGS = "blocksettings";
    public static final String UN_BLOCK_SETTINGS = "unblocksettings";
    public static final String DELETE_GROUP = "groupdelete";
    public static final String DELETE_DEVICE = "deletedevice";
    public static final String SEND_MESSAGES_CREATE_GROUP = "creategroup";
    public static final String SEND_MESSAGES_UPDATE_GROUP = "updategroup";
    private ChildKeepFocusItem childProfile;
    private String family_id;



    private MainDatabaseHelper mDataHelper;
    private String contentNotification = "";

    private static final String TAG = MyGcmPushReceiver.class.getSimpleName();
    private NotificationController notificationController;


    /**
     * Called when message is received.
     *
     * @param from   SenderID of the sender.
     * @param bundle Data bundle containing message data as key/value pairs.
     *               For Set of keys use data.keySet().
     */

    @Override
    public void onMessageReceived(String from, Bundle bundle) {
        String message = bundle.getString("message");
        String title = bundle.getString("tickerText");
        Log.d(TAG, "onMessageReceived: " + bundle.toString());






        if(title.equals("")) {
            title = bundle.getString("title");
        }
        //String title2 = bundle.getString("title");
        Log.d(TAG,"title : " + title);
        Log.d(TAG, "From: " + from);
        //Log.d(TAG, "bundle: " + bundle);
        Log.e(TAG, "Message: " + message);
        mDataHelper = new MainDatabaseHelper(getApplicationContext());


        handleNotification(title, message);

        //sendNotification(message, title);



/*
        {"Data":{
            "Name": "ABDC",
                    "Active": "FALSE",
                    "Day": "Mon, Tue, Sun"
        }}*/

        //sendNotification(message);

        /*kFDHelper = new MainDatabaseHelper(
                getApplicationContext());
        ChildKeepFocusItem childProfile;
        if (kFDHelper.getAllKeepFocusFromDb().size() == 0) {
            childProfile = new ChildKeepFocusItem();
            kFDHelper.addNewFocusItem(childProfile);
        } else {
            childProfile = kFDHelper.getAllKeepFocusFromDb().get(0);
        }

                JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(message);
            JSONObject data = jsonObj.getJSONObject("Data");
            String name_Keepfocus = data.getString("Name");
            String active = data.getString("Active");
            String day = data.getString("Day");
            *//*String appPackage = data.getString("Application");
            ChildAppItem app1 = new ChildAppItem();
            try {
                String appName = appName = (String) getPackageManager().getApplicationLabel(getPackageManager().getApplicationInfo(appPackage, PackageManager.GET_META_DATA));
                app1.setNamePackage(appName);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            ArrayList<ChildAppItem> listApp = childProfile.getListAppFocus();
            boolean conflict = false;
            for(int i = 0; i < listApp.size(); i++){
                if(listApp.get(i).getNamePackage() == appPackage) conflict = true;
            }
            if(!conflict) kFDHelper.addAppItemToDb(app1, childProfile.getKeepFocusId());*//*

            childProfile.setNameFocus(name_Keepfocus);
            childProfile.setDayFocus(day);
            if(active.equals("TRUE")){
                childProfile.setActive(true);
            } else {
                childProfile.setActive(false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        kFDHelper.updateFocusItem(childProfile);
        Intent intent = new Intent();
        intent.setAction(MainUtils.UPDATE_CHILD_SCHEDULER);
        getApplicationContext().sendBroadcast(intent);*/
    }

    public void handleNotification(String title, String message) {
        SharedPreferences prefs = this.getSharedPreferences(
                MainUtils.PACKET_APP, Context.MODE_PRIVATE);
        String titleText = title;
        JSONObject jsonObj = null;
        try {
            switch (titleText){
                case DELETE_NOTI:
                    //Delete
                    Log.e(TAG, "delete");
                    if (SetupWizardActivity.getModeDevice(getApplicationContext()) == Constants.Children
                            && SetupWizardActivity.getTypeJoin(getApplicationContext()) == Constants.JoinSuccess) {
                        deletScheduler(message);
                    }
                    break;
                case CREATE_NOTI:
                    //Create
                    Log.e(TAG, "create");
                    if (SetupWizardActivity.getModeDevice(getApplicationContext()) == Constants.Children
                            && SetupWizardActivity.getTypeJoin(getApplicationContext()) == Constants.JoinSuccess) {
                        createNewScheduler(message);
                    } else if (SetupWizardActivity.getModeDevice(getApplicationContext()) == Constants.Manager) {
                        //createChildSchedulerInManager(message);
                    }
                    break;
                case UPDATE_NOTI:
                    //Update
                    Log.e(TAG, "update");
                    if (SetupWizardActivity.getModeDevice(getApplicationContext()) == Constants.Children
                            && SetupWizardActivity.getTypeJoin(getApplicationContext()) == Constants.JoinSuccess) {
                        updateScheduler(message);
                    }
                    break;
                case JOIN_GROUP:
                    //Join
                    Log.e(TAG, "join");
                    jsonObj = new JSONObject(message);
                    setJoinGroup(jsonObj);
                    break;
                case REPLACE_DEVICE:
                    //Replace
                    //handle noficiation replace here
                    Log.e(TAG, "replace");
                    if (SetupWizardActivity.getModeDevice(getApplicationContext()) == Constants.Admin) {
                        jsonObj = new JSONObject(message);
                        setReplaceDevice(jsonObj);
                    } else if (SetupWizardActivity.getModeDevice(getApplicationContext()) == Constants.Manager) {
                        JSONObject thisMessage = new JSONObject(message);
                        if (thisMessage.getString("tickerText").equals(SEND_MESSAGES_CREATE_GROUP)) {
                            handleNotification(SEND_MESSAGES_CREATE_GROUP, message);
                        }
                    }
                    break;
                case BLOCKALL:
                    Log.e(TAG, "BLOCKALL");
                    if (SetupWizardActivity.getModeDevice(getApplicationContext()) == Constants.Children
                            && SetupWizardActivity.getTypeJoin(getApplicationContext()) == Constants.JoinSuccess) {
                        sendNotificationNoPressAction("Block all", "Your access to your device has been removed.");
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean(MainUtils.IS_BLOCK_ALL, true);
                        editor.putBoolean(MainUtils.IS_ALLOW_ALL, false);
                        editor.commit();
                    }
                    break;
                case UNBLOCKALL:
                    Log.e(TAG, "UNBLOCKALL");
                    if (SetupWizardActivity.getModeDevice(getApplicationContext()) == Constants.Children
                            && SetupWizardActivity.getTypeJoin(getApplicationContext()) == Constants.JoinSuccess) {
                        sendNotificationNoPressAction("Unblock all", "Your device's schedule is now on");
                        SharedPreferences.Editor editor2 = prefs.edit();
                        editor2.putBoolean(MainUtils.IS_BLOCK_ALL, false);
                        editor2.commit();
                    }
                    break;
                case ALLOWALL:
                    Log.e(TAG, "ALLOWALL");
                    if (SetupWizardActivity.getModeDevice(getApplicationContext()) == Constants.Children
                            && SetupWizardActivity.getTypeJoin(getApplicationContext()) == Constants.JoinSuccess) {
                        sendNotificationNoPressAction("Allow all", "You have been given full access to your phone/tablet");
                        SharedPreferences.Editor editor3 = prefs.edit();
                        editor3.putBoolean(MainUtils.IS_BLOCK_ALL, false);
                        editor3.putBoolean(MainUtils.IS_ALLOW_ALL, true);
                        editor3.commit();
                    }
                    break;
                case UNALLOWALL:
                    Log.e(TAG, "UNALLOWALL");
                    if (SetupWizardActivity.getModeDevice(getApplicationContext()) == Constants.Children
                            && SetupWizardActivity.getTypeJoin(getApplicationContext()) == Constants.JoinSuccess) {
                        sendNotificationNoPressAction("Unallow all", "Your device's schedule is now on");
                        SharedPreferences.Editor editor4 = prefs.edit();
                        editor4.putBoolean(MainUtils.IS_ALLOW_ALL, false);
                        editor4.commit();
                    }
                    break;

                case BLOCK_SETTINGS:
                    Log.e(TAG, "BLOCK_SETTINGS");
                    if (SetupWizardActivity.getModeDevice(getApplicationContext()) == Constants.Children
                            && SetupWizardActivity.getTypeJoin(getApplicationContext()) == Constants.JoinSuccess) {
                        sendNotificationNoPressAction("Block settings app", "Your device was blocked settings app");
                        SharedPreferences.Editor editor5 = prefs.edit();
                        editor5.putBoolean(MainUtils.IS_BLOCK_SETTINGS, true);
                        editor5.commit();
                    }
                    break;
                case UN_BLOCK_SETTINGS:
                    Log.e(TAG, "UN_BLOCK_SETTINGS");
                    if (SetupWizardActivity.getModeDevice(getApplicationContext()) == Constants.Children
                            && SetupWizardActivity.getTypeJoin(getApplicationContext()) == Constants.JoinSuccess) {
                        sendNotificationNoPressAction("Un Block settings app", "Your device was blocked settings app");
                        SharedPreferences.Editor editor6 = prefs.edit();
                        editor6.putBoolean(MainUtils.IS_BLOCK_SETTINGS, false);
                        editor6.commit();
                    }
                    break;
                case MANAGER_JOIN_GROUP:
                    //manager join group
                    Log.e(TAG, "MANAGER_JOIN_GROUP");
                    JSONObject messageObject = new JSONObject(message);
                    JSONObject jsonMessage = messageObject.getJSONObject("message");
                    JSONObject jsonDevice = jsonMessage.getJSONObject("Device");
                    String deviceName = jsonDevice.getString("device_name");
                    JSONObject jsonGroup = jsonMessage.getJSONObject("Group");

                    sendNotificationConfirm(deviceName+" want to join your family as a manager.",
                            "You have a manager request",
                            jsonDevice.toString(),
                            jsonGroup.toString());
                    break;
                case ACCEPT_MANAGER_JOIN:
                    //update manager device when join group
                    Log.e(TAG, "ACCEPT_MANAGER_JOIN");
                    SetupWizardActivity.setTypeJoin(Constants.JoinSuccess, getApplicationContext());
                    sendNotificationAccept("Tap here to manage your family.","You've accepted to become a additional manager.");
                    Intent intentAccept = new Intent();
                    intentAccept.setAction(MainUtils.MANAGER_JOIN_SUCCESS);
                    getApplicationContext().sendBroadcast(intentAccept);
                    break;
                case REJECT_MANAGER_JOIN:
                    //update manager device when join group
                    Log.e(TAG, "REJECT_MANAGER_JOIN");
                    SetupWizardActivity.setTypeJoin(Constants.JoinFail, getApplicationContext());
                    sendNotificationReject("Tap here to request again.", "Your request have been rejected.");
                    Intent intentReject = new Intent();
                    intentReject.setAction(MainUtils.EXIT_MANAGER_TO_SETUPWIZARD);
                    getApplicationContext().sendBroadcast(intentReject);
                    break;
                case DELETE_GROUP:
                    Log.e(TAG, "DELETE_GROUP");
                    //update manager device when join group
                    if (SetupWizardActivity.getModeDevice(getApplicationContext()) == Constants.Children) {
                        SetupWizardActivity.setTypeJoin(Constants.JoinFail, getApplicationContext());
                        clearChildData();
                        Intent intentDelete = new Intent();
                        intentDelete.setAction(MainUtils.EXIT_CHILD_TO_SETUPWIZARD);
                        getApplicationContext().sendBroadcast(intentDelete);
                        sendNotificationReject("Tap here to join again.","Your family has been deleted");
                    } else if (SetupWizardActivity.getModeDevice(getApplicationContext()) == Constants.Manager) {
                        JSONObject messageDeleteGroup = new JSONObject(message) ;
                        JSONObject group = messageDeleteGroup.getJSONObject("message");
                        String group_code = group.getString("group_code");
                        ParentGroupItem parent = mDataHelper.getGroupByCode(group_code);
                        mDataHelper.deleteGroupItemById(parent.getId_group());
                        Intent intentUpdateGroup = new Intent();
                        intentUpdateGroup.setAction(MainUtils.UPDATE_FAMILY_GROUP);
                        getApplicationContext().sendBroadcast(intentUpdateGroup);
                    }

                    //if(isActivityRunning(ChildSchedulerActivity.class)) {
                        //Intent intent = new Intent(this, SetupWizardActivity.class);
                        //startActivity(intent);
                    //}


                    break;
                case DELETE_DEVICE:
                    Log.e(TAG, "DELETE_DEVICE");
                    if (SetupWizardActivity.getModeDevice(getApplicationContext()) == Constants.Children) {
                        SetupWizardActivity.setTypeJoin(Constants.JoinFail, getApplicationContext());
                        //clearChildDeviceData();
                        clearChildData();
                        Intent updateIntent = new Intent();
                        updateIntent.setAction(MainUtils.EXIT_CHILD_TO_SETUPWIZARD);
                        getApplicationContext().sendBroadcast(updateIntent);
                        sendNotificationReject("Tap here to join again.", "Your device has been deleted from family");
                    }else if (SetupWizardActivity.getModeDevice(getApplicationContext()) == Constants.Manager){
                        JSONObject messageDeleteDevice = new JSONObject(message);
                        JSONObject device = messageDeleteDevice.getJSONObject("message");
                        int id_server = device.getInt("id");
                        ParentMemberItem parentMemberItem = mDataHelper.getMemberItemByIdServer(id_server);
                        mDataHelper.deleteMemberItemById(parentMemberItem.getId_member());
                        Intent intentUpdateMember = new Intent();
                        intentUpdateMember.setAction(MainUtils.UPDATE_CHILD_DEVICE);
                        getApplicationContext().sendBroadcast(intentUpdateMember);
                    }
                    break;

                case SEND_MESSAGES_CREATE_GROUP:
                    Log.d(TAG,"SEND_MESSAGES_CREATE_GROUP");
                    if (SetupWizardActivity.getModeDevice(getApplicationContext()) == Constants.Manager) {
                        //manager update group created by parent
                        updateGroupCreatedByParent(message);
                    }
                    break;
                case SEND_MESSAGES_UPDATE_GROUP:
                    Log.d(TAG,"SEND_MESSAGES_UPDATE_GROUP");

                    break;

                default:
                    Log.d(TAG,"title not match : " + titleText);
                    break;
            }

            /*if(titleText.equals(DELETE_NOTI)){
                //Delete
                deletScheduler(message);
            } else if (titleText.equals(CREATE_NOTI)){
                Log.d(TAG, "create: " + jsonObj);
                //Create
                createNewScheduler(message);
            } else if (titleText.equals(UPDATE_NOTI)){
                //Update
                updateScheduler(message);
            } else if (titleText.equals(JOIN_GROUP)){
                //Join
                setJoinGroup(jsonObj);
            } else {
                //Another
            }*/
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean isActive(int state){
        if(state == 1) return true;
        else return false;
    }

    private void updateGroupCreatedByParent(String groupCreateMessage) throws JSONException {
        JSONObject messageCreate = new JSONObject(groupCreateMessage);
        JSONObject groupCreated = messageCreate.getJSONObject("message");
        ParentGroupItem newGroup = new ParentGroupItem();
        newGroup.setId_group_server(groupCreated.getInt("id"));
        newGroup.setGroup_name(groupCreated.getString("group_name"));
        newGroup.setGroup_code(groupCreated.getString("group_code"));
        //newGroup.setIs_restore(1);
        mDataHelper.addGroupItemParent(newGroup);
        Intent intentUpdateGroup = new Intent();
        intentUpdateGroup.setAction(MainUtils.UPDATE_FAMILY_GROUP);
        getApplicationContext().sendBroadcast(intentUpdateGroup);
        sendNotificationAccept("Tap here to manage new family.","New family has been created by parent.");

    }

    protected Boolean isActivityRunning(Class activityClass)
    {
        ActivityManager activityManager = (ActivityManager) getBaseContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (activityClass.getCanonicalName().equalsIgnoreCase(task.baseActivity.getClassName()))
                return true;
        }

        return false;
    }

    public void clearChildData(){
        ArrayList<ChildKeepFocusItem> chilList = mDataHelper.getAllKeepFocusFromDb();
        if(chilList!=null) {
            for (int i = 0; i < chilList.size(); i++) {
                mDataHelper.deleteFocusItemById(chilList.get(i).getKeepFocusId());
            }
        }
    }

    public void clearChildDeviceData(){
        ArrayList<ChildKeepFocusItem> childList = mDataHelper.getAllKeepFocusFromDb();
        if(childList!=null) {
            for (int i = 0; i < childList.size(); i++) {
                ArrayList<ChildTimeItem> listTimeItems = childList.get(i).getListTimeFocus();
                for (int j = 0; j < listTimeItems.size(); j ++){
                    mDataHelper.deleteTimeItemById(listTimeItems.get(j).getTimeId());
                }
                mDataHelper.deleteFocusItemById(childList.get(i).getKeepFocusId());
            }
        }
    }


    public void createNewScheduler(String message) throws JSONException {
        JSONObject data = new JSONObject(message);
        JSONObject scheduler = data.getJSONObject("Scheduler");
        JSONArray timeItem = data.getJSONArray("TimeItems");
        Log.d(TAG,"timeItem "+timeItem);
        childProfile = new ChildKeepFocusItem();
        childProfile.setNameFocus(scheduler.getString("scheduler_name"));
        childProfile.setActive(isActive(scheduler.getInt("isActive")));
        childProfile.setDayFocus(scheduler.getString("days"));
        childProfile.setId_profile_server(scheduler.getInt("id"));



        ArrayList<ChildTimeItem> arrayList = new ArrayList(timeItem.length());
        for(int i=0;i < timeItem.length();i++){
            ChildTimeItem item1 = new ChildTimeItem();
            item1.setKeepFocusId(timeItem.getJSONObject(i).getInt("scheduler_id"));
            item1.setHourBegin(timeItem.getJSONObject(i).getInt("start_hours"));
            item1.setHourEnd(timeItem.getJSONObject(i).getInt("end_hours"));
            item1.setMinusBegin(timeItem.getJSONObject(i).getInt("start_minutes"));
            item1.setMinusEnd(timeItem.getJSONObject(i).getInt("end_minutes"));
            item1.setTimeId(timeItem.getJSONObject(i).getInt("id"));
            arrayList.add(item1);
        }

        childProfile.setListTimeFocus(arrayList);

        //childProfile = new ChildKeepFocusItem();
        mDataHelper.addNewFocusItem(childProfile);
        //mDataHelper.updateFocusItem(childProfile);
        Intent intent = new Intent();
        intent.setAction(MainUtils.UPDATE_CHILD_SCHEDULER);
        getApplicationContext().sendBroadcast(intent);
        sendNotificationCreate("", "New schedule has been created");
    }

    public void createChildSchedulerInManager(String message) throws JSONException {
        JSONObject data = new JSONObject(message);
        JSONObject scheduler = data.getJSONObject("Scheduler");
        JSONArray timeItem = data.getJSONArray("TimeItems");
        Log.d(TAG,"timeItem "+timeItem);
        ParentProfileItem parentProfileItem = new ParentProfileItem();

        parentProfileItem.setName_profile(scheduler.getString("scheduler_name"));
        parentProfileItem.setActive(isActive(scheduler.getInt("isActive")));
        parentProfileItem.setDay_profile(scheduler.getString("days"));
        parentProfileItem.setId_profile_server(scheduler.getInt("id"));



        ArrayList<ParentTimeItem> arrayList = new ArrayList(timeItem.length());
        for(int i=0;i < timeItem.length();i++){
            ParentTimeItem item1 = new ParentTimeItem();
            item1.setId_profile(timeItem.getJSONObject(i).getInt("scheduler_id"));
            item1.setHourBegin(timeItem.getJSONObject(i).getInt("start_hours"));
            item1.setHourEnd(timeItem.getJSONObject(i).getInt("end_hours"));
            item1.setMinusBegin(timeItem.getJSONObject(i).getInt("start_minutes"));
            item1.setMinusEnd(timeItem.getJSONObject(i).getInt("end_minutes"));
            item1.setId_time_server(timeItem.getJSONObject(i).getInt("id"));
            arrayList.add(item1);
        }

        parentProfileItem.setListTimer(arrayList);

        //ParentMemberItem parentMemberItem = mDataHelper.getMemberItemByIdServer(scheduler.getInt("groupuser_id"));
        //parentMemberItem.getListProfile().add(parentProfileItem);
        //mDataHelper.updateMemberItem(parentMemberItem);
        Intent intent = new Intent();
        intent.setAction(MainUtils.UPDATE_SCHEDULER);
        getApplicationContext().sendBroadcast(intent);
        sendNotificationCreate("", "New schedule has been created");
    }

    public void updateScheduler(String message) throws JSONException {
        JSONObject data = new JSONObject(message);
        JSONObject scheduler = data.getJSONObject("Scheduler");
        JSONArray timeItem = data.getJSONArray("TimeItems");
        Log.d(TAG, "timeItem " + timeItem);


        ArrayList<ChildKeepFocusItem> chilList = mDataHelper.getAllKeepFocusFromDb();
        if(chilList!=null){
            MainUtils.childKeepFocusItem = chilList.get(0);//for null case
            for(int i=0;i < chilList.size();i++){
                if(scheduler.getInt("id") == chilList.get(i).getId_profile_server()){
                    MainUtils.childKeepFocusItem = chilList.get(i);
                    Log.d(TAG,"id "+chilList.get(i).getId_profile_server());
                }
            }
            MainUtils.childKeepFocusItem.setNameFocus(scheduler.getString("scheduler_name"));
            MainUtils.childKeepFocusItem.setActive(isActive(scheduler.getInt("isActive")));
            MainUtils.childKeepFocusItem.setDayFocus(scheduler.getString("days"));

            //ArrayList<ChildTimeItem> arrayList = new ArrayList(timeItem.length());
            for(int i=0;i < timeItem.length();i++){
                ChildTimeItem item1 = new ChildTimeItem();
                item1.setKeepFocusId(timeItem.getJSONObject(i).getInt("scheduler_id"));
                item1.setHourBegin(timeItem.getJSONObject(i).getInt("start_hours"));
                item1.setHourEnd(timeItem.getJSONObject(i).getInt("end_hours"));
                item1.setMinusBegin(timeItem.getJSONObject(i).getInt("start_minutes"));
                item1.setMinusEnd(timeItem.getJSONObject(i).getInt("end_minutes"));
                item1.setTimeId(timeItem.getJSONObject(i).getInt("id"));

                mDataHelper.addTimeItemToDb(item1,
                            MainUtils.childKeepFocusItem.getKeepFocusId());
                MainUtils.childKeepFocusItem.getListTimeFocus()
                            .add(item1);
            }


            //MainUtils.childKeepFocusItem.setId_profile_server(data.getInt("id"));

            mDataHelper.updateFocusItem(MainUtils.childKeepFocusItem);
            Intent intent = new Intent();
            intent.setAction(MainUtils.UPDATE_CHILD_SCHEDULER);
            getApplicationContext().sendBroadcast(intent);
            sendNotificationCreate("", "SetLimitz has updated your schedule");

        }else {//if null, create new
            createNewScheduler(message);
        }



    }


    public void deletScheduler(String message) throws JSONException {
        JSONObject data = new JSONObject(message);
        JSONObject scheduler = data.getJSONObject("Scheduler");

        ArrayList<ChildKeepFocusItem> chilList = mDataHelper.getAllKeepFocusFromDb();
        if(chilList!=null){
            for(int i=0;i < chilList.size();i++){
                if(scheduler.getInt("id") == chilList.get(i).getId_profile_server()){
                    mDataHelper.deleteFocusItemById(chilList.get(i).getKeepFocusId());
                }
            }
        }
        Intent intent = new Intent();
        intent.setAction(MainUtils.UPDATE_CHILD_SCHEDULER);
        getApplicationContext().sendBroadcast(intent);
        sendNotificationCreate("", "A schedule has been deleted");

    }



    public void setJoinGroup(JSONObject data) throws JSONException {

        Log.d(TAG,"JsonObject join data : " + data);
        JSONObject messages = data.getJSONObject("message");
        ParentMemberItem joinDevice = new ParentMemberItem();
        family_id = data.getString("FamilyID");
        //String group_code = data.getString("FamilyID");//not have now

        //String group_code = "MKXS7E";//for test
        if(family_id !=null) {
            MainUtils.parentGroupItem = mDataHelper.getGroupByCode(family_id);

        }
        //MainUtils.parentGroupItem = mDataHelper.getGroupByCode(group_code);

        //for test

        if (MainUtils.parentGroupItem == null) {
            MainUtils.parentGroupItem = mDataHelper.getAllGroupItemParent().get(0);//add to first
        }
        //ParentGroupItem joinToGroup = new ParentGroupItem();
        Log.d(TAG,"join To Group "+MainUtils.parentGroupItem.getGroup_name());
        //MainUtils.parentGroupItem = joinToGroup;

        int type;
        if(messages.getString("device_mode").trim().equals(JoinGroupActivity.MANAGER)) {
            Log.d(TAG,"type manager " + messages.getString("device_mode").trim());
            type = 1;
        } else{
            Log.d(TAG,"type child " + messages.getString("device_mode").trim());
            type = 0;
        }
        try {
            joinDevice.setId_member_server(messages.getInt("id"));
            joinDevice.setName_member(messages.getString("device_name"));
            joinDevice.setType_member(type);

            mDataHelper.addMemberItemParent(joinDevice,MainUtils.parentGroupItem.getId_group());
            Log.e("thong.nv", "MainUtils.parentGroupItem.getListMember() before " + MainUtils.parentGroupItem.getListMember().size());
            MainUtils.parentGroupItem.getListMember().add(joinDevice);
            mDataHelper.makeDetailOneGroupItemParent(MainUtils.parentGroupItem);

            contentNotification = "Device name: "+ messages.getString("device_name")
                    /*+ ", Model " + messages.getString("device_model")
                    + ", Mode " + messages.getString("device_mode")
                    + ", Type " + messages.getString("device_type")*/;
            Log.e("thong.nv", "MainUtils.parentGroupItem.getListMember() after " + MainUtils.parentGroupItem.getListMember().size());
            Log.e("thong.nv", "MainUtils = " + MainUtils.parentGroupItem);
            sendNotification(contentNotification, "Device added to SetLimitz");

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void setReplaceDevice(JSONObject data) throws JSONException{
        Log.d("vinh","JsonObject replace data : " + data);
        JSONObject messages = data.getJSONObject("message");
        int memberIDServer = messages.getInt("id");
        ArrayList<ParentMemberItem> listMemberParent = MainUtils.parentGroupItem.getListMember();
        for (int i = 0; i < listMemberParent.size(); i ++){
             if (listMemberParent.get(i).getId_member_server() == memberIDServer){
                 listMemberParent.get(i).setName_member(messages.getString("device_name"));
                 mDataHelper.updateMemberItem(listMemberParent.get(i));
             }
        }
        /*ParentMemberItem replaceDevice = mDataHelper.getMemberItemById();
        Log.d("vinh","JsonObject replaceDevice : " + replaceDevice);
        replaceDevice.setName_member(messages.getString("device_name"));
        mDataHelper.updateMemberItem(replaceDevice);*/
        contentNotification = "Device " +messages.getString("device_name");
        sendNotification(contentNotification, "A device has been replaced in your family");
    }

    private void sendNotification(String message, String title) {
        Intent intent = new Intent(this, DeviceMemberManagerment.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icon_app)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        Notification noti = notificationBuilder.build();
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        NotificationManager notificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, noti);
    }

    private void sendNotificationCreate(String message, String title) {
        Intent intent = new Intent(this, ChildSchedulerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icon_app)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        Notification noti = notificationBuilder.build();
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        NotificationManager notificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, noti);
    }

    private void sendNotificationAccept(String message, String title) {
        Intent intent = new Intent(this, FamilyManagerment.class);
        intent.putExtra("NotificationAccept",true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icon_app)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        Notification noti = notificationBuilder.build();
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        NotificationManager notificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, noti);
    }

    private void sendNotificationReject(String message, String title) {
        Intent intent = new Intent(this, SetupWizardActivity.class);
        intent.putExtra("NotificationAccept",true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icon_app)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        Notification noti = notificationBuilder.build();
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        NotificationManager notificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(3 /* ID of notification */, noti);
    }

    private void sendNotificationNoPressAction(String message, String title) {
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icon_app)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri);
        Notification noti = notificationBuilder.build();
        noti.flags |= Notification.FLAG_AUTO_CANCEL;
        NotificationManager notificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, noti);
    }

    private void sendNotificationConfirm(String message, String title, String deviceObject, String familyObject) {

        //int time = (int)System.currentTimeMillis();

        Intent intentAccept = new Intent(this, NotificationButtonListener.class);
        intentAccept.putExtra("Confirm",true);
        intentAccept.putExtra("Device",deviceObject);
        intentAccept.putExtra("Group",familyObject);
        //intentAccept.putExtra("ID",time);
        PendingIntent pendingIntentAccept = PendingIntent.getBroadcast(this, 0 /* Request code */, intentAccept,
                PendingIntent.FLAG_ONE_SHOT);
        //
        Intent intentDeny = new Intent(this, NotificationButtonListener.class);
        intentDeny.putExtra("Confirm",false);
        intentDeny.putExtra("Device",deviceObject);
        intentDeny.putExtra("Group",familyObject);
        //intentDeny.putExtra("ID",time);
        PendingIntent pendingIntentDeny = PendingIntent.getBroadcast(this, 1 /* Request code */, intentDeny,
                PendingIntent.FLAG_ONE_SHOT);
        //

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icon_app)
                .setContentTitle(title)
                .setContentText(message)
                .addAction(R.color.transparent,"Accept",pendingIntentAccept)
                .addAction(R.color.transparent,"Reject",pendingIntentDeny)
                .setAutoCancel(true)
                .setSound(defaultSoundUri);
        Notification noti = notificationBuilder.build();

        NotificationManager notificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        noti.flags |= Notification.FLAG_NO_CLEAR;
        notificationManager.notify(11, noti);
    }

    public static class NotificationButtonListener extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "NotificationButtonListener onReceive " + intent);
            boolean confirm = intent.getBooleanExtra("Confirm", false);
            //int idNoti = intent.getIntExtra("ID", 0);
            Log.e(TAG, "confirm " + confirm);
            String device = intent.getStringExtra("Device");
            String group = intent.getStringExtra("Group");
            GroupRequestController groupRequestController = new GroupRequestController(context);
            if (confirm) {
                groupRequestController.managerJoinGroup(device,group,0);
            } else {
                groupRequestController.managerJoinGroup(device,group,1);
            }
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(11);
        }

    }
}

