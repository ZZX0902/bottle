package com.example.zhangzhaoxiang.bottle;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.BIND_AUTO_CREATE;


public class wenjian extends Fragment {

    static public String TAG = "MusicListActivity";
    //设置ListView创建列表
    private List<MusicItem> mMusicList;
    private ListView mMusicListView;
    private Button mPlayBtn;
    private Button mPreBtn;
    private Button mNextBtn;
    private TextView mMusicTitle;
    private TextView mPlayedTime;
    private TextView mDurationTime;
    private SeekBar mMusicSeekBar;
    //创建一个MusicUpdateTask对象
    private MusicUpdateTask mMusicUpdateTask;
    private TextView textView;
    private Button button;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //不同的Activity对应不同的布局
        View view = inflater.inflate(R.layout.fragment_wenjian, container, false);
        mMusicList = new ArrayList<MusicItem>();
        mMusicListView = (ListView) view.findViewById(R.id.music_list);
        MusicItemAdapter adapter = new MusicItemAdapter(getActivity(), R.layout.music_item, mMusicList);
        mMusicListView.setAdapter(adapter);
        mMusicListView.setOnCreateContextMenuListener(this);
        //设置监听器，监听音乐列表的点击事件
        mMusicListView.setOnItemClickListener(mOnMusicItemClickListener);
        //播放和暂停使用的按钮
        mPlayBtn = (Button) view.findViewById(R.id.play_btn);
        //上一首
        mPreBtn = (Button) view.findViewById(R.id.pre_btn);
        //下一首
        mNextBtn = (Button) view.findViewById(R.id.next_btn);
        //音乐名称
        mMusicTitle = (TextView) view.findViewById(R.id.music_title);
        //播放时长
        mDurationTime = (TextView) view.findViewById(R.id.duration_time);
        //当前播放时间
        mPlayedTime = (TextView) view.findViewById(R.id.played_time);
        //进度条
        mMusicSeekBar = (SeekBar) view.findViewById(R.id.seek_music);
        mMusicSeekBar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);

        mMusicUpdateTask = new MusicUpdateTask();
        mMusicUpdateTask.execute();
        //明确指定那个Service启动
        Intent i = new Intent(getActivity(), MusicService.class);
        //启动MusicService
        getActivity().startService(i);
        //实现绑定操作
        getActivity().bindService(i, mServiceConnection, BIND_AUTO_CREATE);
        return view;
    }
    @Override
    //在主界面所在的MusicListActivity销毁时取消MusicUpdateTask的运行
    public void onDestroy() {
        super.onDestroy();
        if(mMusicUpdateTask != null && mMusicUpdateTask.getStatus() == AsyncTask.Status.RUNNING) {
            mMusicUpdateTask.cancel(true);
        }
        mMusicUpdateTask = null;
        //注销监听函数
        mMusicService.unregisterOnStateChangeListener(mStateChangeListenr);
        //当MusicListActivity退出时，将MusicService解除绑定
        getActivity().unbindService(mServiceConnection);
        //手动回收使用的图片资源
        for(MusicItem item : mMusicList) {
            if( item.thumb != null ) {
                item.thumb.recycle();
                item.thumb = null;
            }
        }
        mMusicList.clear();
    }
    //进度条拖动的监听器
    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            //停止拖动时，根据进度条的位置来设定播放的位置
            if(mMusicService != null) {
                mMusicService.seekTo(seekBar.getProgress());
            }
        }
    };
    //修改监听器，实现单击音乐添加到播放列表中
    private AdapterView.OnItemClickListener mOnMusicItemClickListener = new AdapterView.OnItemClickListener() {

        @Override

        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            if(mMusicService != null) {
                //点击响应处，添加播放音乐的代码
                //通过MusicService提供的接口，把要添加的音乐交给MusicService处理
                mMusicService.addPlayList(mMusicList.get(position));
            }
        }
    };
    //定义一个MultiChoiceModeListener将菜单和菜单被点击后的响应事件添加进去

    //控制区域的状态是否为可操作状态
    private void enableControlPanel(boolean enabled) {
        mPlayBtn.setEnabled(enabled);
        mPreBtn.setEnabled(enabled);
        mNextBtn.setEnabled(enabled);
        mMusicSeekBar.setEnabled(enabled);
    }
    //更新播放信息
    private void updatePlayingInfo(MusicItem item) {
        //将毫秒单位的时间，转化为00:00形式的格式
        String times = Utils.convertMSecendToTime(item.duration);
        mDurationTime.setText(times);

        times = Utils.convertMSecendToTime(item.playedTime);
        mPlayedTime.setText(times);
        //设置进度条最大值
        mMusicSeekBar.setMax((int) item.duration);
        //设置进度条当前值
        mMusicSeekBar.setProgress((int) item.playedTime);

        mMusicTitle.setText(item.name);
    }

    //注册监听函数，为了获取MusicService的状态
    private MusicService.OnStateChangeListenr mStateChangeListenr = new MusicService.OnStateChangeListenr() {

        @Override
        public void onPlayProgressChange(MusicItem item) {
            //更新播放进度信息
            updatePlayingInfo(item);
        }

        @Override
        public void onPlay(MusicItem item) {
            //更新播放按钮背景
            mPlayBtn.setBackgroundResource(R.mipmap.ic_pause);
            updatePlayingInfo(item);
            //激活控制区域
            enableControlPanel(true);
        }

        @Override
        public void onPause(MusicItem item) {
            //更新播放按钮背景
            mPlayBtn.setBackgroundResource(R.mipmap.ic_play);
            //激活控制区域
            enableControlPanel(true);
        }
    };
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        menu.add(1, 1000, 0, "使用此条录音");
        super.onCreateContextMenu(menu, v, menuInfo);
    }
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1000:
                Toast.makeText(getActivity(), "使用此条录音", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), choosetime.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        boolean b = super.onContextItemSelected(item);
        Log.d("11111111111", b+"");

        return super.onContextItemSelected(item);
    }
    //创建一个AsyncTask，在它的doBackground（）方法中进行查询操作
    private class MusicUpdateTask extends AsyncTask<Object, MusicItem, Void> {
        //工作线程，处理耗时的查询音乐的操作
        @Override
        protected Void doInBackground(Object... params) {
            //查询外部存储地址上的音乐文件
            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            //查询音乐文件所使用的字段名称
            String[] searchKey = new String[] {
                    //对应文件在数据库中的检索ID
                    MediaStore.Audio.Media._ID,
                    //对于那个文件的标题
                    MediaStore.Audio.Media.TITLE,
                    //对应文件所在的专辑ID
                    MediaStore.Audio.Albums.ALBUM_ID,
                    //对应文件的存放位置
                    MediaStore.Audio.Media.DATA,
                    //对应文件的播放时长
                    MediaStore.Audio.Media.DURATION
            };
            //查询到的文件路径中包含music这个字段的所有文件
            String where = MediaStore.Audio.Media.DATA + " like \"%"+getString(R.string.search_path)+"%\"";
            String [] keywords = null;
            //设定为默认排序方式
            String sortOrder = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;
            //获取ContentResolver对象，并向Media Provider发起查询请求，查询结果存放在Cursor（指标）当中
            ContentResolver resolver = getActivity().getContentResolver();
            Cursor cursor = resolver.query(uri, searchKey, where, keywords, sortOrder);
            if(cursor != null)
            {
                //遍历Cursor，得到它指向的每一条查询到的信息，当其指向某条数据时，获取它携带的每个字段值
                while(cursor.moveToNext() && ! isCancelled())
                {
                    //获取音乐的路径
                    String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                    //获取音乐ID
                    String id = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                    //通过Uri和Id组合出该音乐的特有Uri地址
                    Uri musicUri = Uri.withAppendedPath(uri, id);
                    //获取音乐的名称
                    String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                    //获取音乐的时长，单位是毫秒
                    long duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));

                    //该音乐所在专辑ID
                    int albumId = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM_ID));
                    //通过AlbumID组合出专辑的Uri地址
                    Uri albumUri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), albumId);
                    //创建一个MusicItem对象
                    MusicItem data = new MusicItem(musicUri, albumUri, name, duration, 0);
                    if (uri != null) {
                        ContentResolver res = getActivity().getContentResolver();
                        data.thumb = Utils.createThumbFromUir(res, albumUri);
                    }
                    Log.d(TAG, "real music found: " + path);
                    publishProgress(data);
                }
                //cursor使用完后要关闭
                cursor.close();
            }
            return null;
        }
        @Override
        protected void onProgressUpdate(MusicItem... values) {
            //主线程，把要显示的音乐添加到音乐的展示列表中
            MusicItem data = values[0];
            mMusicList.add(data);
            MusicItemAdapter adapter = (MusicItemAdapter) mMusicListView.getAdapter();
            adapter.notifyDataSetChanged();
        }
    }//MusicListActivity绑定MusicService的时候，先定义一个ServiceConnection
    private MusicService.MusicServiceIBinder mMusicService;
    //创建一个ServiceConnection，当绑定Service后，在onServiceConnection（）中会得到Service返回的Binder
    private ServiceConnection mServiceConnection = new ServiceConnection()
    {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //这个service参数，就是Service当中onBind（）返回的Binder
            //获取访问Service的桥梁 --MusicServiceIBinder
            //绑定成功后取得MusicService提供的接口
            mMusicService = (MusicService.MusicServiceIBinder) service;
            //注册监听器
            mMusicService.registerOnStateChangeListener(mStateChangeListenr);
            //获取播放列表中可播放的音乐
            MusicItem item = mMusicService.getCurrentMusic();
            if(item == null) {
                //没有可播放的音乐时，控制区域不可操作
                enableControlPanel(false);
                return;
            }
            else {
                //根据当前被加载的音乐信息更新控制区域信息
                updatePlayingInfo(item);
            }
            if(mMusicService.isPlaying()) {
                //如果音乐处于播放状态则按钮背景设置为暂停图标
                mPlayBtn.setBackgroundResource(R.mipmap.ic_pause);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    //显示播放列表的实现


    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().findViewById(R.id.play_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    //播放音乐按钮
                    case R.id.play_btn: {
                        //启动Service
                        if(mMusicService != null) {
                            if(!mMusicService.isPlaying()) {
                                //开始播mMusicService.play();
                            }
                            else {
                                //暂停播放
                                mMusicService.pause();
                            }
                        }
                    }
                    break;
                }
            }
        });
        getActivity().findViewById(R.id.pre_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    //播放音乐按钮
                    //下一曲按钮
                    case R.id.pre_btn: {
                        if(mMusicService != null) {
                            mMusicService.playPre();
                        }
                    }
                    break;
                    //上一曲按钮
                }
            }
        });
        getActivity().findViewById(R.id.next_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {

                    //下一曲按钮
                    case R.id.next_btn: {
                        if(mMusicService != null) {
                            mMusicService.playNext();
                        }
                    }
                    break;
                }
            }
        });
        /**
         * TODO 实现底部菜单对应布局控件事件
         * */
    }
}
