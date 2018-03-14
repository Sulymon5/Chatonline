package com.chatonx.application.presenters.groups;

import com.chatonx.application.activities.groups.AddMembersToGroupActivity;
import com.chatonx.application.api.APIService;
import com.chatonx.application.app.ChatonxApplication;
import com.chatonx.application.helpers.AppHelper;
import com.chatonx.application.interfaces.Presenter;
import com.chatonx.application.api.apiServices.UsersService;

import io.realm.Realm;

/**
 * Created by Abderrahim El imame on 26/03/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class AddMembersToGroupPresenter implements Presenter {
    private final AddMembersToGroupActivity view;
    private final Realm realm;


    public AddMembersToGroupPresenter(AddMembersToGroupActivity addMembersToGroupActivity) {
        this.view = addMembersToGroupActivity;
        this.realm = ChatonxApplication.getRealmDatabaseInstance();

    }


    @Override
    public void onStart() {

    }

    @Override
    public void onCreate() {

        APIService mApiService = APIService.with(view);
        UsersService mUsersContacts = new UsersService(realm, view, mApiService);
        mUsersContacts.getLinkedContacts().subscribe(view::ShowContacts, throwable -> AppHelper.LogCat("AddMembersToGroupPresenter "+throwable.getMessage()));

    }


    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestroy() {

        realm.close();
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onStop() {

    }
}