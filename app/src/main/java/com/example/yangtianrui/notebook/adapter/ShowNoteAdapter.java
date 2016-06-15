package com.example.yangtianrui.notebook.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.yangtianrui.notebook.R;
import com.example.yangtianrui.notebook.util.TextFormatUtil;

/**
 * Created by yangtianrui on 16-5-22.
 */
public class ShowNoteAdapter extends CursorAdapter {

    private Context context;
    private Cursor cursor;

    public ShowNoteAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        this.context = context;
        this.cursor = cursor;
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater1 = LayoutInflater.from(context);
        View view = inflater1.inflate(R.layout.item_note, null, false);
        ViewHolder holder = new ViewHolder();
        holder.mTvTitle = (TextView) view.findViewById(R.id.id_tv_note_title);
        holder.mTvContent = (TextView) view.findViewById(R.id.id_tv_note_summary);
        holder.mTvCreateTime = (TextView) view.findViewById(R.id.id_tv_note_create_time);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        String title = cursor.getString(cursor.getColumnIndex("title"));
        holder.mTvTitle.setText(title);
        holder.mTvContent.setText(TextFormatUtil.getNoteSummary(cursor.getString(cursor.getColumnIndex("content"))));
        holder.mTvCreateTime.setText(cursor.getString(cursor.getColumnIndex("create_time")));
    }

    final class ViewHolder {
        TextView mTvTitle;
        TextView mTvContent;
        TextView mTvCreateTime;
    }
}
