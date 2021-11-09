package papayacoders.instastory;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

import io.reactivex.observers.DisposableObserver;
import papayacoders.instastory.api.CommonAPI;
import papayacoders.instastory.models.FullDetailModel;
import papayacoders.instastory.models.ItemModel;
import papayacoders.instastory.models.StoryModel;
import papayacoders.instastory.models.TrayModel;
import papayacoders.instastory.utils.SharePrefs;

public class Stories {

    private static final MutableLiveData<ArrayList<TrayModel>> mList = new MutableLiveData<>();
    public static LiveData<ArrayList<TrayModel>> list = mList;
    private static final MutableLiveData<ArrayList<ItemModel>> mStoryList = new MutableLiveData<>();
    public static LiveData<ArrayList<ItemModel>> storyList = mStoryList;

    public static AlertDialog dialog;

    public static void users(Context context) {
        if (SharePrefs.getInstance(context).getBoolean(SharePrefs.IS_INSTAGRAM_LOGIN)) {
            getStoriesApi(context);
        } else {
            Toast.makeText(context, "Please login first.", Toast.LENGTH_SHORT).show();
//            initLoginDialog(context);
        }
    }

    public static void login(Context context) {
        ((Activity) context).startActivityForResult(new Intent(context, LoginActivity.class), 100);

    }

//    private static void initLoginDialog(Context context) {
//        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.SheetDialog);
//        bottomSheetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        bottomSheetDialog.setContentView(R.layout.dialog_login);
//        bottomSheetDialog.show();
//        TextView textViewYes = bottomSheetDialog.findViewById(R.id.textViewYes);
//        textViewYes.setOnClickListener(new View.OnClickListener() {
//            public final void onClick(View view) {
//
//                bottomSheetDialog.dismiss();
//
//                if (!SharePrefs.getInstance(context).getBoolean(SharePrefs.IS_INSTAGRAM_LOGIN)) {
//
//                    return;
//                }
//
//            }
//        });
//
//        TextView textViewCancel = bottomSheetDialog.findViewById(R.id.textViewCancel);
//        textViewCancel.setOnClickListener(view -> bottomSheetDialog.dismiss());
//    }


    //    @Override
//    public void onActivityResult(int i, int i2, Intent intent) {
//        try {
//            super.onActivityResult(i, i2, intent);
//            if (i != 100 || i2 != -1) {
//                return;
//            }
//            if (SharePrefs.getInstance(this).getBoolean(SharePrefs.IS_INSTAGRAM_LOGIN)) {
////                this.textViewLogin.setText(getResources().getString(R.string.logout));
////                this.linearLayoutStories.setVisibility(View.VISIBLE);
////                this.linearLayoutPlaceHolder.setVisibility(View.GONE);
//                Toast.makeText(this, "login", Toast.LENGTH_SHORT).show();
//                getStoriesApi(this);
//                return;
//            }
//            Toast.makeText(this, "logout", Toast.LENGTH_SHORT).show();
////            this.textViewLogin.setText(getResources().getString(R.string.login));
////            this.linearLayoutStories.setVisibility(View.GONE);
////            this.linearLayoutPlaceHolder.setVisibility(View.VISIBLE);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public static void getStories(Context context, String str) {

        CommonAPI commonAPI = CommonAPI.getInstance((Activity) context);
        try {
            if (!isNetworkAvailable(context)) {
                Toast.makeText(context, context.getResources().getString(R.string.no_net_conn), Toast.LENGTH_SHORT).show();
            } else if (commonAPI != null) {
//                progressLoadingBar.setVisibility(View.VISIBLE);
                CommonAPI commonAPI1 = commonAPI;
                DisposableObserver<FullDetailModel> disposableObserver = storyDetailObserver;
                commonAPI1.getFullFeed(disposableObserver, str, "ds_user_id=" + SharePrefs.getInstance(context).getString(SharePrefs.USERID) + "; sessionid=" +
                        SharePrefs.getInstance(context).getString(SharePrefs.SESSIONID));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static DisposableObserver<FullDetailModel> storyDetailObserver = new DisposableObserver<FullDetailModel>() {
        public void onNext(FullDetailModel fullDetailModel) {
//            recyclerViewUser.setVisibility(View.VISIBLE);
//            progressLoadingBar.setVisibility(View.GONE);
            try {
                mStoryList.postValue(fullDetailModel.getReelFeed().getItems());

//                storyAdapter = new StoryAdapter(MainActivity.this, fullDetailModel.getReelFeed().getItems());
//                recyclerViewStories.setAdapter(storyAdapter);
//                storyAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(Throwable th) {
//            progressLoadingBar.setVisibility(View.GONE);
            th.printStackTrace();
        }

        @Override
        public void onComplete() {
//            progressLoadingBar.setVisibility(View.GONE);
        }
    };


    private static void getStoriesApi(Context context) {

        dialog = new androidx.appcompat.app.AlertDialog.Builder(context)
                .setMessage("Loading....")
                .setCancelable(false)
                .create();

        dialog.show();


        CommonAPI commonAPI = CommonAPI.getInstance((Activity) context);
        try {
            if (!isNetworkAvailable(context)) {
                Toast.makeText(context, "No Internet", Toast.LENGTH_SHORT).show();
            } else if (commonAPI != null) {
//                progressLoadingBar.setVisibility(View.VISIBLE);
                CommonAPI commonAPI1 = commonAPI;
                DisposableObserver<StoryModel> disposableObserver = storyObserver;
                commonAPI1.getStories(disposableObserver, "ds_user_id=" +
                        SharePrefs.getInstance(context).getString(SharePrefs.USERID) + "; sessionid=" +
                        SharePrefs.getInstance(context).getString(SharePrefs.SESSIONID));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static DisposableObserver<StoryModel> storyObserver = new DisposableObserver<StoryModel>() {

        public void onNext(StoryModel storyModel) {
//            recyclerViewUser.setVisibility(View.VISIBLE);
//            progressLoadingBar.setVisibility(View.GONE);

            Log.d("StoryModal", "onNext: " + storyModel);
            try {
                ArrayList<TrayModel> arrayList = new ArrayList<>();
                for (int i = 0; i < storyModel.getTray().size(); i++) {
                    try {
                        if (storyModel.getTray().get(i).getUser().getFullname() != null) {
                            arrayList.add(storyModel.getTray().get(i));
                        }
                    } catch (Exception ignored) {
                    }
                }

                mList.postValue(arrayList);
                if (dialog != null)
                    dialog.dismiss();

                Log.d("MISTER", "onNext: " + arrayList);

//                profileAdapter = new ProfileAdapter(MainActivity.this, arrayList, MainActivity.this);
//                recyclerViewUser.setAdapter(profileAdapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(Throwable th) {
//            progressLoadingBar.setVisibility(View.GONE);
            th.printStackTrace();
        }

        @Override
        public void onComplete() {
//            progressLoadingBar.setVisibility(View.GONE);

        }
    };


    public static boolean isLogin(Context context) {
        return SharePrefs.getInstance(context).getBoolean(SharePrefs.IS_INSTAGRAM_LOGIN);

    }

    public static void logout(Context context) {
        SharePrefs.getInstance(context).putBoolean(SharePrefs.IS_INSTAGRAM_LOGIN, false);
        SharePrefs.getInstance(context).putString(SharePrefs.COOKIES, "");
        SharePrefs.getInstance(context).putString(SharePrefs.CSRF, "");
        SharePrefs.getInstance(context).putString(SharePrefs.SESSIONID, "");
        SharePrefs.getInstance(context).putString(SharePrefs.USERID, "");
        mList.postValue(null);
//        if (SharePrefs.getInstance(context).getBoolean(SharePrefs.IS_INSTAGRAM_LOGIN).booleanValue()) {
//            textViewLogin.setText(getResources().getString(R.string.logout));
//            linearLayoutStories.setVisibility(View.VISIBLE);
//            linearLayoutPlaceHolder.setVisibility(View.GONE);
//        } else {
//            textViewLogin.setText(getResources().getString(R.string.login));
//            linearLayoutStories.setVisibility(View.GONE);
//            linearLayoutPlaceHolder.setVisibility(View.VISIBLE);
//        }
    }
}
