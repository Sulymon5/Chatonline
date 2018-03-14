package com.chatonx.application.presenters.users;


import android.Manifest;
import android.os.Handler;
import android.support.v7.app.AlertDialog;

import com.chatonx.application.R;
import com.chatonx.application.activities.NewConversationContactsActivity;
import com.chatonx.application.activities.PrivacyActivity;
import com.chatonx.application.api.APIService;
import com.chatonx.application.api.apiServices.UsersService;
import com.chatonx.application.app.ChatonxApplication;
import com.chatonx.application.helpers.AppHelper;
import com.chatonx.application.helpers.PermissionHandler;
import com.chatonx.application.helpers.PreferenceManager;
import com.chatonx.application.helpers.UtilsPhone;
import com.chatonx.application.interfaces.Presenter;
import com.chatonx.application.models.users.contacts.ContactsModel;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;

import static java.lang.Thread.sleep;

/**
 * Created by Abderrahim El imame on 20/02/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class ContactsPresenter implements Presenter {
    private NewConversationContactsActivity newConversationContactsActivity;
    private PrivacyActivity privacyActivity;
    private Realm realm;
    private UsersService mUsersContacts;


    public ContactsPresenter(NewConversationContactsActivity newConversationContactsActivity) {
        this.newConversationContactsActivity = newConversationContactsActivity;
        this.realm = ChatonxApplication.getRealmDatabaseInstance();

    }


    public ContactsPresenter(PrivacyActivity privacyActivity) {
        this.privacyActivity = privacyActivity;
        this.realm = ChatonxApplication.getRealmDatabaseInstance();
    }


    @Override
    public void onStart() {
    }

    @Override
    public void onCreate() {
        if (privacyActivity != null) {
            APIService mApiService = APIService.with(privacyActivity);
            mUsersContacts = new UsersService(realm, privacyActivity, mApiService);
            getPrivacyTerms();
        } else if (newConversationContactsActivity != null) {
            APIService mApiService = APIService.with(newConversationContactsActivity);
            mUsersContacts = new UsersService(realm, newConversationContactsActivity, mApiService);
            getContacts();
        }

    }


    public void getContacts() {
        if (newConversationContactsActivity != null) {
            if (mUsersContacts.getLinkedContactsSize() == 0) {
                loadDataFromServer();
            }
            try {
                sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                mUsersContacts.getAllContacts().subscribe(contactsModels -> {
                    newConversationContactsActivity.ShowContacts(contactsModels);
                }, throwable -> {
                    newConversationContactsActivity.onErrorLoading(throwable);
                }, () -> {
                    newConversationContactsActivity.onHideLoading();
                });
                try {
                    PreferenceManager.setContactSize(newConversationContactsActivity, mUsersContacts.getLinkedContactsSize());
                } catch (Exception e) {
                    AppHelper.LogCat(" Exception size contact fragment");
                }
            } catch (Exception e) {
                AppHelper.LogCat("getAllContacts Exception ContactsPresenter "+e.getMessage());
            }
        }
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestroy() {
        if (!realm.isClosed())
            realm.close();
    }

    @Override
    public void onLoadMore() {

    }


    @Override
    public void onRefresh() {
        if (newConversationContactsActivity != null) {
            if (PermissionHandler.checkPermission(newConversationContactsActivity, Manifest.permission.READ_CONTACTS)) {
                AppHelper.LogCat("Read contact data permission already granted.");
                newConversationContactsActivity.onShowLoading();

                Observable.create((ObservableOnSubscribe<List<ContactsModel>>) subscriber -> {
                    try {
                        List<ContactsModel> contactsModels = UtilsPhone.GetPhoneContacts();
                        subscriber.onNext(contactsModels);
                        subscriber.onComplete();
                    } catch (Exception throwable) {
                        subscriber.onError(throwable);
                    }
                }).subscribeOn(Schedulers.computation()).subscribe(contacts -> {
                    mUsersContacts.updateContacts(contacts).subscribe(contactsModelList -> {
                        // newConversationContactsActivity.ShowContacts(contactsModelList);
                        try {
                            sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        try {
                            mUsersContacts.getAllContacts().subscribe(contactsModels -> {
                                newConversationContactsActivity.ShowContacts(contactsModels);
                            }, throwable -> {
                                newConversationContactsActivity.onErrorLoading(throwable);
                            }, () -> {
                                newConversationContactsActivity.onHideLoading();
                            });
                            try {
                                PreferenceManager.setContactSize(newConversationContactsActivity, mUsersContacts.getLinkedContactsSize());
                            } catch (Exception e) {
                                AppHelper.LogCat(" Exception size contact fragment");
                            }
                        } catch (Exception e) {
                            AppHelper.LogCat("getAllContacts Exception ContactsPresenter ");
                        }
                        AppHelper.CustomToast(newConversationContactsActivity, newConversationContactsActivity.getString(R.string.success_response_contacts));
                    }, throwable -> {
                        newConversationContactsActivity.onErrorLoading(throwable);
                        AlertDialog.Builder alert = new AlertDialog.Builder(newConversationContactsActivity.getApplicationContext());
                        alert.setMessage(newConversationContactsActivity.getString(R.string.error_response_contacts));
                        alert.setPositiveButton(R.string.ok, (dialog, which) -> {
                        });
                        alert.setCancelable(false);
                        alert.show();
                    }, () -> {
                        newConversationContactsActivity.onHideLoading();
                    });
                }, throwable -> {
                    AppHelper.LogCat(" " + throwable.getMessage());
                });

                mUsersContacts.getContactInfo(PreferenceManager.getID(newConversationContactsActivity)).subscribe(contactsModel -> AppHelper.LogCat("getContactInfo"), AppHelper::LogCat);

            } else {
                AppHelper.LogCat("Please request Read contact data permission.");
                PermissionHandler.requestPermission(newConversationContactsActivity, Manifest.permission.READ_CONTACTS);
            }
        }


    }

    @Override
    public void onStop() {

    }


    private void loadDataFromServer() {

        Observable.create((ObservableOnSubscribe<List<ContactsModel>>) subscriber -> {
            try {
                List<ContactsModel> contactsModels = UtilsPhone.GetPhoneContacts();
                subscriber.onNext(contactsModels);
                subscriber.onComplete();
            } catch (Exception throwable) {
                subscriber.onError(throwable);
            }
        }).subscribeOn(Schedulers.computation()).subscribe(contacts -> {
            mUsersContacts.updateContacts(contacts).subscribe(contactsModelList -> {
                //newConversationContactsActivity.ShowContacts(contactsModelList);
                new Handler().postDelayed(() -> {
                    mUsersContacts.getContactInfo(PreferenceManager.getID(newConversationContactsActivity)).subscribe(contactsModel -> AppHelper.LogCat("info user ContactsPresenter"), throwable -> AppHelper.LogCat("On error ContactsPresenter"));
                }, 2000);
            }, throwable -> {
                newConversationContactsActivity.onErrorLoading(throwable);
            }, () -> {

            });
        }, throwable -> {
            AppHelper.LogCat(" " + throwable.getMessage());
        });

    }


    private void getPrivacyTerms() {
        mUsersContacts.getPrivacyTerms().subscribe(statusResponse -> {
            if (statusResponse.isSuccess()) {
                privacyActivity.showPrivcay(statusResponse.getMessage());
            } else {
                AppHelper.LogCat(" " + statusResponse.getMessage());
            }

        }, throwable -> {
            AppHelper.LogCat(" " + throwable.getMessage());
        });
    }
}