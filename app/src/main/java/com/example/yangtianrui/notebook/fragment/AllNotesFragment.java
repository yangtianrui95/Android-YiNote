package com.example.yangtianrui.notebook.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.yangtianrui.notebook.R;
import com.example.yangtianrui.notebook.activity.MainActivity;
import com.example.yangtianrui.notebook.activity.NoteDetailActivity;
import com.example.yangtianrui.notebook.adapter.ShowNoteAdapter;
import com.example.yangtianrui.notebook.bean.Note;
import com.example.yangtianrui.notebook.config.Constants;
import com.example.yangtianrui.notebook.db.NoteDAO;
import com.example.yangtianrui.notebook.service.AutoSyncService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by yangtianrui on 16-5-21.
 * 显示所有Note,使用Loader实现异步加载
 */
public class AllNotesFragment extends Fragment implements AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor>, SwipeRefreshLayout.OnRefreshListener {
    private NoteDAO mNoteDAO;
    private ListView mLvNotes;
    private SwipeRefreshLayout mSrlRefresh;
    private CursorAdapter mAdapter;
    private Cursor mCursor;
    private final static int CONTEXT_UPDATE_ORDER = 0;
    private final static int CONTEXT_DELETE_ORDER = 1;
    private View root;

    private List<BmobObject> mSyncNotes = new ArrayList<>();
    private Set<Note> mAllNotes = new HashSet<>();

    // 自动同步功能需使用
    private Timer mTimer;
    private SyncStateReceiver mReceiver;

    public AllNotesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNoteDAO = new NoteDAO(getActivity());
        // 查询所有行
        mCursor = mNoteDAO.queryNote(null, null);
        // 获取同步信息,启动Service的计划任务
        if (MainActivity.IS_SYNC) {
            Intent intent = new Intent(getActivity(), AutoSyncService.class);
            getActivity().startService(intent);
            // 注册广播
            mReceiver = new SyncStateReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(Constants.SYNC_BROADCAST_ACTION);
            getActivity().registerReceiver(mReceiver, filter);
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater
            , ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_all_note, container, false);
        mLvNotes = (ListView) root.findViewById(R.id.id_lv_all_note);

        mSrlRefresh = (SwipeRefreshLayout) root.findViewById(R.id.id_srl_refresh);
        mSrlRefresh.setColorSchemeColors(R.color.colorPrimary);
        mSrlRefresh.setSize(SwipeRefreshLayout.DEFAULT);
        mSrlRefresh.setProgressViewEndTarget(true, 200);
        // 在下拉刷新时同步数据
        mSrlRefresh.setOnRefreshListener(this);

        mAdapter = new ShowNoteAdapter(getActivity(), mCursor);
        getLoaderManager().initLoader(0, null, this);
        mLvNotes.setAdapter(mAdapter);
        mLvNotes.setOnItemClickListener(this);
        registerForContextMenu(mLvNotes);
        return root;
    }

    /**
     * 此时重启Loader机制更新数据
     */
    @Override
    public void onResume() {
        super.onResume();
        mCursor = mNoteDAO.queryNote(null, null);
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mCursor.close();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            getActivity().unregisterReceiver(mReceiver);
            Intent intent = new Intent(getActivity(), AutoSyncService.class);
            getActivity().stopService(intent);
        }
    }

    /**
     * 上下文菜单的回调函数
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        //int position = mAdapter.getItem(info.position);
        int position = info.position; // list中的位置
        Cursor c = (Cursor) mAdapter.getItem(position); // CursorAdapter中getItem()返回特定的cursor对象
        int itemID = c.getInt(c.getColumnIndex("_id"));
        switch (item.getOrder()) {
            case CONTEXT_UPDATE_ORDER: // 更新操作
                //Toast.makeText(getActivity(),"UPDATE",Toast.LENGTH_SHORT).show();
                break;
            case CONTEXT_DELETE_ORDER: // 删除操作
                //Toast.makeText(getActivity(),"DELETE",Toast.LENGTH_SHORT).show();
                mNoteDAO.deleteNote("_id=?", new String[]{itemID + ""});
                getLoaderManager().restartLoader(0, null, this);
                break;
        }
        return super.onContextItemSelected(item);
    }

    /**
     * 创建上下文菜单
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Enter your choice:");
        menu.add(0, v.getId(), CONTEXT_UPDATE_ORDER, "Update");
        menu.add(0, v.getId(), CONTEXT_DELETE_ORDER, "Delete");
    }

    // 跳转到详情页
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor c = (Cursor) mAdapter.getItem(position); // CursorAdapter中getItem()返回特定的cursor对象
        int itemID = c.getInt(c.getColumnIndex("_id"));
//        Log.v("LOG", "AllNoteFragment itemID: " + itemID);
        Intent intent = new Intent(getActivity(), NoteDetailActivity.class);
        intent.putExtra(NoteDetailActivity.SENDED_NOTE_ID, itemID);
        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = Uri.parse("content://com.terry.NoteBook");

        return new CursorLoader(getActivity(), uri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }


    /**
     * 下拉刷新时,向Bmob后台同步数据
     */
    @Override
    public void onRefresh() {
        final Uri uri = Uri.parse("content://com.terry.NoteBook");
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Note note = new Note();
                int noteID = cursor.getInt(0);
                note.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                note.setContent(cursor.getString(cursor.getColumnIndex("content")));
                note.setCreateTime(cursor.getString(cursor.getColumnIndex("create_time")));
                // 将数据库中所有数据添加到集合中
                mAllNotes.add(note);
                // 获取当前用户
                note.setUserName(BmobUser.getCurrentUser(getActivity()).getUsername());
                if (cursor.getString(cursor.getColumnIndex("is_sync")).equals("false")) {
                    ContentValues values = new ContentValues();
                    values.put("is_sync", "true");
                    getActivity().getContentResolver().update(uri, values, "_id=?", new String[]{noteID + ""});
                    mSyncNotes.add(note);
                }
            }
            cursor.close();
            // 批量向服务器上传数据数据
            new BmobObject().insertBatch(getActivity(), mSyncNotes, new SaveListener() {
                @Override
                public void onSuccess() {

                    mSyncNotes.clear();
                    // 向服务器下载本机没有的数据
                    BmobQuery<Note> bmobQuery = new BmobQuery<>();
                    bmobQuery.addWhereEqualTo("userName", BmobUser.getCurrentUser(getActivity()).getUsername());
                    bmobQuery.setLimit(50); // 返回50条数据
                    // 从服务器获取数据
                    bmobQuery.findObjects(getActivity(), new FindListener<Note>() {
                        @Override
                        public void onSuccess(List<Note> list) {
                            // 获取所有没有在服务器中的数据
                            list.removeAll(mAllNotes);
//                            Log.v("LOG", "allNote:" + mAllNotes);
//                            Log.v("LOG", "List: " + list + "");
                            ContentResolver resolver = getActivity().getContentResolver();
                            // 将此数据写入数据库中
                            for (Note note : list) {
                                ContentValues values = new ContentValues();
                                values.put("title", note.getTitle());
                                values.put("content", note.getContent());
                                values.put("create_time", note.getCreateTime());
                                values.put("is_sync", "true");
                                resolver.insert(uri, values);
                            }
                            mAllNotes.clear();
                            mSrlRefresh.setRefreshing(false);
                            // 通知UI更新界面
                            getLoaderManager().restartLoader(0, null, AllNotesFragment.this);
                            Snackbar.make(root, "同步完成", Snackbar.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(int i, String s) {
                            Snackbar.make(root, s, Snackbar.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onFailure(int i, String s) {
                    mSrlRefresh.setRefreshing(false);
                    Toast.makeText(getActivity(), "更新失败 " + s, Toast.LENGTH_SHORT).show();
                }
            });

        }
    }


    /**
     * 接收Service发送的广播,更新UI
     */
    class SyncStateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String state = intent.getStringExtra(AutoSyncService.SEND_SYNC_STATE);
            Snackbar.make(root, state, Snackbar.LENGTH_SHORT).show();
        }
    }

}
