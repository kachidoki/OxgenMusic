package com.kachidoki.oxgenmusic.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kachidoki.oxgenmusic.R;
import com.kachidoki.oxgenmusic.app.BaseActivity;
import com.kachidoki.oxgenmusic.config.Constants;
import com.kachidoki.oxgenmusic.model.AdapterSearch;
import com.kachidoki.oxgenmusic.model.bean.SearchResult;
import com.kachidoki.oxgenmusic.model.bean.Song;
import com.kachidoki.oxgenmusic.network.NetWork;
import com.kachidoki.oxgenmusic.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by mayiwei on 16/12/13.
 */
public class SearchActivity extends BaseActivity {
    @BindView(R.id.search_keyword)
    EditText searchKeyword;
    @BindView(R.id.search_search)
    TextView searchSearch;
    @BindView(R.id.search_recycler)
    RecyclerView searchRecycler;
    @BindView(R.id.loadFreshing)
    LinearLayout freshing;
    @BindView(R.id.loadFail)
    LinearLayout loadFail;

    AdapterSearch adapter = new AdapterSearch(SearchActivity.this);

    Observer<List<Song>> observer = new Observer<List<Song>>() {
        @Override
        public void onCompleted() {
        }
        @Override
        public void onError(Throwable e) {
            freshing.setVisibility(View.GONE);
            loadFail.setVisibility(View.VISIBLE);
            Toast.makeText(SearchActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onNext(List<Song> songs) {
            freshing.setVisibility(View.GONE);
            adapter.setData(songs);
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        setToolbar(true);
        freshing.setVisibility(View.GONE);

        searchRecycler.setLayoutManager(new LinearLayoutManager(this));
        searchRecycler.setAdapter(adapter);
        searchSearch.setEnabled(false);
        searchKeyword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i== EditorInfo.IME_ACTION_SEARCH){
                    getSearchMusic();
                    Utils.closeInputMethod(SearchActivity.this);
                }
                return false;
            }
        });
        searchKeyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                boolean enable = charSequence.length() != 0;
                searchSearch.setEnabled(enable);
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void getSearchMusic(){
        unsubscribe();
        subscription = NetWork.getMusicApi()
                .getSearchList(Constants.showapi_appid,Constants.showapi_sign,searchKeyword.getText().toString(),1)
                .map(new Func1<SearchResult, List<Song>>() {
                    @Override
                    public List<Song> call(SearchResult searchResult) {
                        return ConvertToSong(searchResult);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    private List<Song> ConvertToSong(SearchResult result){
        List<Song> songs = new ArrayList<>();
        if (result.showapi_res_body.pagebean.contentlist!=null){
            for (SearchResult.SearchContent content:result.showapi_res_body.pagebean.contentlist){
                Song song = new Song();
                song.singername = content.singername;
                song.singerid = content.singerid;
                song.songname = content.songname;
                song.albumid = content.albumid;
                song.albumpic_big = content.albumpic_big;
                song.url = content.m4a;
                songs.add(song);
            }
        }
        return songs;
    }


    @OnClick(R.id.search_search)
    void Search(){
        freshing.setVisibility(View.VISIBLE);
        getSearchMusic();
        Utils.closeInputMethod(SearchActivity.this);
    }




}
