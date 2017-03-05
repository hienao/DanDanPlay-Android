package cn.swt.danmuplayer.main;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.swt.corelib.utils.ProgressDialogUtils;
import com.swt.corelib.utils.TimeUtils;
import com.swt.corelib.utils.ToastUtils;

import cn.swt.danmuplayer.AboutFragment;
import cn.swt.danmuplayer.R;
import cn.swt.danmuplayer.application.MyApplication;
import cn.swt.danmuplayer.core.base.BaseActivity;
import cn.swt.danmuplayer.fileexplorer.beans.VideoFileInfo;
import cn.swt.danmuplayer.fileexplorer.utils.JudgeVideoUtil;
import cn.swt.danmuplayer.fileexplorer.view.ContentsFragment;
import cn.swt.danmuplayer.main.bean.FragmentTag;
import cn.swt.danmuplayer.setting.AddVideoFileManualFragment;
import cn.swt.danmuplayer.setting.SettingFragment;
import io.realm.Realm;

import static cn.swt.danmuplayer.fileexplorer.presenter.ContentPresenter.getVideoThumbnail;
import static cn.swt.danmuplayer.fileexplorer.presenter.ContentPresenter.restoreContentTable;


public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private boolean mNetWorkMode = true;
    private NavigationView mNavigationView;
    private ContentsFragment mContentsFragment;
    //当前页面标记
    FragmentTag mFragmentTag = FragmentTag.CONTENT;
    private AddVideoFileManualFragment mAddVideoFileManualFragment;
    private SettingFragment mSettingFragment;
    private AboutFragment mAboutFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setPERMISSIONS(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE});
        initData();
        initView();
    }

    private void initData() {
        mContentsFragment = new ContentsFragment();
        mAddVideoFileManualFragment = new AddVideoFileManualFragment();
        mSettingFragment = new SettingFragment();
        mAboutFragment=new AboutFragment();
    }

    private void initView() {
        Toolbar toolbar = getToolbar();
        setCustomTitle(getResources().getString(R.string.app_name));
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.setItemIconTintList(null);//menu图标显示原来的颜色
        //网络切换状态初始化
        mNetWorkMode = MyApplication.getSP().getBoolean("network_mode", true);
        MenuItem menuItemTitle = mNavigationView.getMenu().findItem(R.id.nav_switch_network_title);
        MenuItem menuItemSwitch = mNavigationView.getMenu().findItem(R.id.nav_switch_network);
        if (mNetWorkMode) {
            menuItemTitle.setTitle(R.string.switch_network_online);
            menuItemSwitch.setIcon(R.drawable.ic_online);
        } else {
            menuItemTitle.setTitle(R.string.switch_network_offline);
            menuItemSwitch.setIcon(R.drawable.ic_offline);
        }
        //初始化首页
        getSupportFragmentManager().beginTransaction().add(R.id.content_main, mContentsFragment).commit();
        //直接打开文件
        Intent intent = getIntent();
        String action = intent.getAction();
        if(intent.ACTION_VIEW.equals(action)){
            Uri uri = (Uri) intent.getData();
            try {
                String filePath=getPathByUri4kitkat(MainActivity.this,uri);
                Realm realm=MyApplication.getRealmInstance();
                checkAndAddVideoInfo(realm,filePath);
            } catch (Exception e) {
                ToastUtils.showShortToastSafe(MainActivity.this, "系统未识别出视频文件，无法添加");
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 根据tag获取当前显示的fragment
     */
    Fragment getCurrentFragment(FragmentTag indexTag) {
        Fragment fragment = null;
        switch (indexTag) {
            case CONTENT:
                fragment = mContentsFragment;
                break;
            case ADD:
                fragment = mAddVideoFileManualFragment;
                break;
            case SETTING:
                fragment = mSettingFragment;
                break;
            case ABOUT:
                fragment=mAboutFragment;
                break;
            default:
                fragment = mContentsFragment;
                break;
        }
        return fragment;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_switch_network) {
            mNetWorkMode = !mNetWorkMode;
            MyApplication.getSP().putBoolean("network_mode", mNetWorkMode);
            MenuItem menuItemTitle = mNavigationView.getMenu().findItem(R.id.nav_switch_network_title);
            if (mNetWorkMode) {
                menuItemTitle.setTitle(R.string.switch_network_online);
                item.setIcon(R.drawable.ic_online);
            } else {
                menuItemTitle.setTitle(R.string.switch_network_offline);
                item.setIcon(R.drawable.ic_offline);
            }
        } else if (id == R.id.nav_play) {
            if (mContentsFragment.isAdded()) {
                getSupportFragmentManager().beginTransaction().hide(getCurrentFragment(mFragmentTag)).show(mContentsFragment).commit();
            } else {
                getSupportFragmentManager().beginTransaction().hide(getCurrentFragment(mFragmentTag)).add(R.id.content_main, mContentsFragment).commit();
            }
            mFragmentTag = FragmentTag.CONTENT;
            setCustomTitle(getResources().getString(R.string.app_name));
        } else if (id == R.id.nav_add) {
            if (mAddVideoFileManualFragment.isAdded()) {
                getSupportFragmentManager().beginTransaction().hide(getCurrentFragment(mFragmentTag)).show(mAddVideoFileManualFragment).commit();
            } else {
                getSupportFragmentManager().beginTransaction().hide(getCurrentFragment(mFragmentTag)).add(R.id.content_main, mAddVideoFileManualFragment).commit();
            }
            mFragmentTag = FragmentTag.ADD;
            setCustomTitle(getResources().getString(R.string.app_video_file_select));
        } else if (id == R.id.nav_setting) {
            if (mSettingFragment.isAdded()) {
                getSupportFragmentManager().beginTransaction().hide(getCurrentFragment(mFragmentTag)).show(mSettingFragment).commit();
            } else {
                getSupportFragmentManager().beginTransaction().hide(getCurrentFragment(mFragmentTag)).add(R.id.content_main, mSettingFragment).commit();
            }
            mFragmentTag = FragmentTag.SETTING;
            setCustomTitle(getResources().getString(R.string.app_setting));
        } else if (id == R.id.nav_about) {
            if (mAboutFragment.isAdded()) {
                getSupportFragmentManager().beginTransaction().hide(getCurrentFragment(mFragmentTag)).show(mAboutFragment).commit();
            } else {
                getSupportFragmentManager().beginTransaction().hide(getCurrentFragment(mFragmentTag)).add(R.id.content_main, mAboutFragment).commit();
            }
            mFragmentTag = FragmentTag.ABOUT;
            setCustomTitle(getResources().getString(R.string.drawer_about));
        } else if (id == R.id.nav_exit) {
            finish();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    /**
     * 检查数据库中是否有该视频，如果没有就添加
     * @param realm
     * @param newfilepath
     */
    private void checkAndAddVideoInfo(Realm realm,String newfilepath){
        //检测文件在数据库中是否存在
        VideoFileInfo videoFileInfoc = realm.where(VideoFileInfo.class).equalTo("videoPath", newfilepath).findFirst();
        if (videoFileInfoc == null) {
            if (!JudgeVideoUtil.isVideo(newfilepath)) {
                ToastUtils.showShortToastSafe(MainActivity.this, "请选择视频文件！");
                ProgressDialogUtils.dismissDialog();
                return;
            }
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(newfilepath);
            String videoName = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            String videoDuration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            //检测文件是否后缀
            VideoFileInfo videoFileInfo = new VideoFileInfo();
            videoFileInfo.setVideoPath(newfilepath);
            videoFileInfo.setVideoContentPath(newfilepath.substring(0, newfilepath.lastIndexOf("/") + 1));
            if (TextUtils.isEmpty(videoName)) {
                videoFileInfo.setVideoName(videoName);
                videoFileInfo.setVideoNameWithoutSuffix("Unkonwn");
            } else if (videoName.contains(".")) {
                videoFileInfo.setVideoName(videoName);
                videoFileInfo.setVideoNameWithoutSuffix(videoName.substring(0, videoName.lastIndexOf(".")));
            } else {
                videoFileInfo.setVideoName(videoName);
                videoFileInfo.setVideoNameWithoutSuffix(videoName);
            }
            videoFileInfo.setCover(getVideoThumbnail(newfilepath));
            try {
                videoFileInfo.setVideoLength(TimeUtils.formatDuring(Long.parseLong(videoDuration)));
            } catch (NumberFormatException e) {
                videoFileInfo.setVideoLength("UnKnown");
            }
            realm.beginTransaction();
            realm.copyToRealm(videoFileInfo);
            realm.commitTransaction();
            restoreContentTable();
        } else {
            ToastUtils.showShortToastSafe(MainActivity.this, "数据库中已存在该数据，无法添加");
        }
    }
    /**
     * 专为Android4.4设计的从Uri获取文件绝对路径，以前的方法已不好使
     * @param context
     * @param uri
     * @return
     */
    public static String getPathByUri4kitkat(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {// ExternalStorageProvider
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {// DownloadsProvider
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {// MediaProvider
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[] { split[1] };
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {// MediaStore
            // (and
            // general)
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {// File
            return uri.getPath();
        }
        return null;
    }
    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context
     *            The context.
     * @param uri
     *            The Uri to query.
     * @param selection
     *            (Optional) Filter used in the query.
     * @param selectionArgs
     *            (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
