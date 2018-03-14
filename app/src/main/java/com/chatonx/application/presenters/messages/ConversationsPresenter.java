package com.chatonx.application.presenters.messages;

import com.chatonx.application.api.APIHelper;
import com.chatonx.application.app.AppConstants;
import com.chatonx.application.app.ChatonxApplication;
import com.chatonx.application.fragments.home.ConversationsFragment;
import com.chatonx.application.helpers.AppHelper;
import com.chatonx.application.interfaces.Presenter;
import com.chatonx.application.models.messages.ConversationsModel;
import com.chatonx.application.models.messages.MessagesModel;
import com.chatonx.application.models.users.Pusher;

import org.greenrobot.eventbus.EventBus;

import io.realm.Realm;

/**
 * Created by Abderrahim El imame on 20/02/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class ConversationsPresenter implements Presenter {
    private final ConversationsFragment conversationsFragmentView;
    private final Realm realm;


    public ConversationsPresenter(ConversationsFragment conversationsFragment) {
        this.conversationsFragmentView = conversationsFragment;
        this.realm = ChatonxApplication.getRealmDatabaseInstance();
    }


    @Override
    public void onStart() {
    }

    @Override
    public void onCreate() {
        if (!EventBus.getDefault().isRegistered(conversationsFragmentView))
            EventBus.getDefault().register(conversationsFragmentView);
        loadData(false);


    }


    private void loadData(boolean isRefresh) {
        if (isRefresh)
            conversationsFragmentView.onShowLoading();
        else
            conversationsFragmentView.onProgressShow();
        try {

            APIHelper.initializeApiGroups().updateGroups().subscribe(groupsModelList -> {
                AppHelper.LogCat("groupsModelList " + groupsModelList.size());
            }, throwable -> {

                AppHelper.LogCat("onerror " + throwable.getMessage());
                getConversationFromLocal(isRefresh);
            }, () -> {
                AppHelper.LogCat("oncomplete ");
                getConversationFromLocal(isRefresh);
            });

        } catch (Exception e) {
            AppHelper.LogCat("conversation presenter " + e.getMessage());
        }


    }

    private void getConversationFromLocal(boolean isRefresh) {
        APIHelper.initializeConversationsService().getConversations().subscribe(conversationsModels -> {
            AppHelper.LogCat("conversationsModels " + conversationsModels.size());
            conversationsFragmentView.UpdateConversation(conversationsModels);
            if (isRefresh)
                conversationsFragmentView.onHideLoading();
            else
                conversationsFragmentView.onProgressHide();
        }, conversationsFragmentView::onErrorLoading, () -> {

        });

        if (isRefresh)
            conversationsFragmentView.onHideLoading();
        else
            conversationsFragmentView.onProgressHide();
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(conversationsFragmentView);
        realm.close();
    }

    @Override
    public void onLoadMore() {
    }

    @Override
    public void onRefresh() {
        loadData(true);

    }

    @Override
    public void onStop() {

    }

    public void getGroupInfo(int groupID) {
        AppHelper.LogCat("update image group profile");
        APIHelper.initializeApiGroups().getGroupInfo(groupID).subscribe(groupsModel -> {
            int ConversationID = getConversationGroupId(groupsModel.getId());
            if (ConversationID != 0) {
                realm.executeTransaction(realm1 -> {
                    ConversationsModel conversationsModel = realm1.where(ConversationsModel.class).equalTo("id", ConversationID).findFirst();
                    conversationsModel.setRecipientImage(groupsModel.getGroupImage());
                    conversationsModel.setRecipientUsername(groupsModel.getGroupName());
                    realm1.copyToRealmOrUpdate(conversationsModel);
                    EventBus.getDefault().post(new Pusher(AppConstants.EVENT_UPDATE_CONVERSATION_OLD_ROW, ConversationID));
                });
            }
        }, throwable -> {
            AppHelper.LogCat("Get group info conversation presenter " + throwable.getMessage());
        });
    }

    private int getConversationGroupId(int GroupID) {
        try {
            ConversationsModel conversationsModel = realm.where(ConversationsModel.class).equalTo("groupID", GroupID).findFirst();
            return conversationsModel.getId();
        } catch (Exception e) {
            AppHelper.LogCat("Conversation id Exception ContactFragment" + e.getMessage());
            return 0;
        }
    }

    public void getGroupInfo(int groupID, MessagesModel messagesModel) {
        AppHelper.LogCat("group id exited " + groupID);
        APIHelper.initializeApiGroups().getGroupInfo(groupID).subscribe(groupsModel -> {
            conversationsFragmentView.sendGroupMessage(groupsModel, messagesModel);
        }, throwable -> {
            AppHelper.LogCat("Get group info conversation presenter " + throwable.getMessage());
        });

    }

    public void getGroupInfo(int groupID, int conversationID) {
        AppHelper.LogCat("group id created " + groupID);
        APIHelper.initializeApiGroups().getGroupInfo(groupID).subscribe(groupsModel -> {
            conversationsFragmentView.sendGroupMessage(groupsModel, conversationID);
        }, throwable -> {
            AppHelper.LogCat("Get group info conversation presenter " + throwable.getMessage());
        });
    }
}
