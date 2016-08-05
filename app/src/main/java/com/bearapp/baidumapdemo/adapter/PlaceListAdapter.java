package com.bearapp.baidumapdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.baidu.mapapi.search.core.PoiInfo;
import com.bearapp.baidumapdemo.R;

import java.util.List;

/**
 * Created by Henry.Ren on 16/8/5.
 */
public class PlaceListAdapter extends BaseAdapter {

    private Context context;
    private List<PoiInfo> poiInfoList;
    private LayoutInflater layoutInflater;

    public PlaceListAdapter(Context context, List<PoiInfo> poiInfoList) {
        this.context = context;
        this.poiInfoList = poiInfoList;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return poiInfoList == null ? 0 : poiInfoList.size();
    }

    @Override
    public Object getItem(int i) {
        return poiInfoList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.layout_place_item, null);
            viewHolder = new ViewHolder();
            viewHolder.tvName = (TextView) view.findViewById(R.id.tvName);
            viewHolder.tvAddress = (TextView) view.findViewById(R.id.tvAddress);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        PoiInfo poiInfo = poiInfoList.get(i);
        viewHolder.tvName.setText(poiInfo.name);
        viewHolder.tvAddress.setText(poiInfo.address);
        return view;
    }


    static class ViewHolder {
        TextView tvName;
        TextView tvAddress;
    }
}
