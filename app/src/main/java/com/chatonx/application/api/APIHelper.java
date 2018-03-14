package com.chatonx.application.api;

import com.chatonx.application.api.apiServices.AuthService;
import com.chatonx.application.api.apiServices.ConversationsService;
import com.chatonx.application.api.apiServices.GroupsService;
import com.chatonx.application.api.apiServices.MessagesService;
import com.chatonx.application.api.apiServices.UsersService;
import com.chatonx.application.app.ChatonxApplication;

/**
 * Created by Abderrahim El imame on 4/11/17.
 *
 * @Email : abderrahim.elimame@gmail.com
 * @Author : https://twitter.com/Ben__Cherif
 * @Skype : ben-_-cherif
 */

public class APIHelper {

    public static UsersService initialApiUsersContacts() {
        APIService mApiService = APIService.with(ChatonxApplication.getInstance());
        return new UsersService(ChatonxApplication.getRealmDatabaseInstance(), ChatonxApplication.getInstance(), mApiService);
    }


    public static GroupsService initializeApiGroups() {
        APIService mApiService = APIService.with(ChatonxApplication.getInstance());
        return new GroupsService(ChatonxApplication.getRealmDatabaseInstance(), ChatonxApplication.getInstance(), mApiService);
    }

    public static ConversationsService initializeConversationsService() {
        return new ConversationsService(ChatonxApplication.getRealmDatabaseInstance());
    }

    public static MessagesService initializeMessagesService() {
        return new MessagesService(ChatonxApplication.getRealmDatabaseInstance());
    }

    public static AuthService initializeAuthService() {
        APIService mApiService = APIService.with(ChatonxApplication.getInstance());
        return new AuthService(ChatonxApplication.getInstance(), mApiService);
    }
}
