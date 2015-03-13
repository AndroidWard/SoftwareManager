package com.example.softwaremanager;

import java.util.List;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListViewAdapter extends BaseAdapter {
	private List<PackageInfo> packageInfos = null;
	private LayoutInflater inflater = null;
	private  Context context = null;
		public ListViewAdapter(List<PackageInfo>  packageInfos , Context context){
		this.packageInfos = packageInfos; 
		this.context = context ; 
		inflater = LayoutInflater.from(context);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return packageInfos.size();
			}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return packageInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.listviewitem, null);
		TextView appName = (TextView) view.findViewById(R.id.lv_item_appname);
		TextView packageName = (TextView) view.findViewById(R.id.lv_item_packagename);
		ImageView iv = (ImageView) view.findViewById(R.id.lv_icon);
		appName.setText(packageInfos.get(position).applicationInfo.loadLabel(context.getPackageManager()));
		packageName.setText(packageInfos.get(position).packageName);
		iv.setImageDrawable(packageInfos.get(position).applicationInfo.loadIcon(context.getPackageManager()));	
		return view;
	}

}
