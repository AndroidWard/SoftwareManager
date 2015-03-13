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

public class GridViewAdapter extends BaseAdapter {
	private List<PackageInfo> packageInfos = null;
	private LayoutInflater inflater = null;
	private  Context context = null;

	public GridViewAdapter(List<PackageInfo>  packageInfos , Context context){
		this.packageInfos = packageInfos; 
		this.context = context ; 
		inflater = LayoutInflater.from(context);}
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
		View view = inflater.inflate(R.layout.gridviewitem, null);
		TextView tv = (TextView) view.findViewById(R.id.gv_item_appname);
		ImageView iv = (ImageView) view.findViewById(R.id.gv_item_icon);
		tv.setText(packageInfos.get(position).applicationInfo.loadLabel(context.getPackageManager()));
		iv.setImageDrawable(packageInfos.get(position).applicationInfo.loadIcon(context.getPackageManager()));
		return view;
		
	}

}
