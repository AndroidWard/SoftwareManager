package com.example.softwaremanager;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

public class ShowApp extends Activity implements Runnable,OnClickListener{

	private GridView gridView ;
	private ListView listView ; 
	private List<PackageInfo> packageInfos = null; 
	private List<PackageInfo> userPackageInfos = null;
		private boolean isAllApp = true; 

	private ImageButton changeViewBtn ;
	private ImageButton changeCategoryBtn;
	private boolean isListView = true; 
	private ProgressDialog pd;
	private List<PackageInfo> showPackageInfos = null;
	private   int SEARCH_APP = 0 ;
	private   int DELETE_APP = 1;
	
	
	private Handler handler = new Handler() {
		// 当消息发送过来的时候会执行下面这个方法
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			if(msg.what == SEARCH_APP){
				showPackageInfos = packageInfos;
				gridView.setAdapter(new GridViewAdapter(showPackageInfos, ShowApp.this));
				listView.setAdapter(new ListViewAdapter(showPackageInfos, ShowApp.this));
				//设置标题进度条不可见
				pd.dismiss();
				setProgressBarIndeterminateVisibility(false);
				if(msg.what == DELETE_APP) {
					System.out.println("Delete App Success!!");
				}
			}
		};
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		  requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
	      getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	      setContentView(R.layout.showapp);
	      setProgressBarIndeterminateVisibility(true);
	      
	      AnimationSet set = new AnimationSet(false);
	      Animation animation = new AlphaAnimation(0,1);
	      animation.setDuration(200);
	      set.addAnimation(animation);
	      
	      animation = new TranslateAnimation(1, 13, 10, 50);
	      animation.setDuration(120);
	      set.addAnimation(animation);
	      
	      animation = new RotateAnimation(30,10);
	      animation.setDuration(120);
	      set.addAnimation(animation);
	      
	      animation = new ScaleAnimation(5,0,2,0);
	      animation.setDuration(120);
	      set.addAnimation(animation);
	      
	      LayoutAnimationController controller = new LayoutAnimationController(set, 1);
	      
	      
	      gridView = (GridView) this.findViewById(R.id.gridView);
	      listView = (ListView)this.findViewById(R.id.lv_apps);
	      gridView.setLayoutAnimation(controller);
	      
	     
	       listView.setLayoutAnimation(controller);
	      listView.setCacheColorHint(0);
	      
		
		changeViewBtn = (ImageButton) findViewById(R.id.ib_change_view);
		changeCategoryBtn=(ImageButton)findViewById(R.id.ib_change_category);
		((ImageButton)findViewById(R.id.exit)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		changeViewBtn.setOnClickListener(this);
		changeCategoryBtn.setOnClickListener(this);
		//列表视图和网格视图切换
		gridView.setOnItemClickListener(listener);
		listView.setOnItemClickListener(listener);
		 pd = ProgressDialog.show(this, "请稍候...", "正在搜索你所安装的应用程序...",true,false);
		Thread thread = new Thread(this);
		thread.start();
		//设置标题进度条可见
		setProgressBarIndeterminateVisibility(true);
	}
	OnItemClickListener listener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			//通过position取出对应apk的packageInfo
			final PackageInfo packageInfo = showPackageInfos.get(position);
			//创建一个Dialog用来进行选择
			Builder builder = new AlertDialog.Builder(ShowApp.this);
		      
			builder.setTitle("选项");
			//接收一个资源的ID
			builder.setItems(R.array.choice,new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case 0:
						String packageName = packageInfo.packageName;
						ActivityInfo activityInfo = packageInfo.activities[0];
						//activities数组只有在设置了PackageManager.GET_ACTIVITIES后才会被填充
						//故在获取packageInfo时要在后面加上一个或的条件。
						if(activityInfo == null) {
							Toast.makeText(ShowApp.this, "没有任何activity", Toast.LENGTH_SHORT).show();
							return;
						}
						String activityName = activityInfo.name;
						Intent intent = new Intent();
						//通过包名和类名来启动应用程序
						intent.setComponent(new ComponentName(packageName,activityName));
						//启动apk
						startActivity(intent);
						break;
					case 1:
						//显示apk详细信息
						showAppDetail(packageInfo);
						break;
					case 2:
						Uri packageUri = Uri.parse("package:" + packageInfo.packageName);
						Intent deleteIntent = new Intent();
						deleteIntent.setAction(Intent.ACTION_DELETE);
						deleteIntent.setData(packageUri);
						//采用这句话是为了：解决删除完应用后，程序图标仍然存在的Bug。它会调用onActivityResult方法
						startActivityForResult(deleteIntent, 0);
						break;
					}
				}
			});
			//此处设为null，因为默认就实现了关闭功能
			builder.setNegativeButton("取消", null);
			
			builder.create().show();
		}
	};
		@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//获得所有apk
		packageInfos = getPackageManager().getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES | PackageManager.GET_ACTIVITIES);
		userPackageInfos = new ArrayList<PackageInfo>();
		for(int i=0;i<packageInfos.size();i++) {
			
			PackageInfo temp = packageInfos.get(i);
			ApplicationInfo appInfo = temp.applicationInfo;
			boolean flag = false;
			if((appInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
				flag = true;
				//FLAG_SYSTEM表明是系统apk
			} else if((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				// 用户apk
				flag = true;
			}
			if(flag) {
				//添加到系统apk数组中
				userPackageInfos.add(temp);
			}
		}	
		if(isAllApp) {
			showPackageInfos = packageInfos;
		} else {
			showPackageInfos = userPackageInfos;
		}
		gridView.setAdapter(new GridViewAdapter(showPackageInfos,ShowApp.this));
		listView.setAdapter(new ListViewAdapter(showPackageInfos,ShowApp.this));		
	}
	//显示apk的详细信息
	private void showAppDetail(PackageInfo packageInfo) {	
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("详细信息");
		StringBuffer message = new StringBuffer();
		message.append("程序名称:" + packageInfo.applicationInfo.loadLabel(getPackageManager()));
		message.append("\n 包名:" + packageInfo.packageName);//包名
		message.append("\n 版本号:" + packageInfo.versionCode);//版本号
		message.append("\n 版本名:" + packageInfo.versionName);//版本名		
		builder.setMessage(message.toString());
		builder.setIcon(packageInfo.applicationInfo.loadIcon(getPackageManager()));
		builder.setPositiveButton("确定", null);//仅仅是让Dialog消失
		builder.create().show();
	}
		// 这个新开辟的线程主要用来把ListView给填充满，以避免它阻塞主线程
	@Override
	protected void onResume() {
		super.onResume();
	}
	@Override
	public void run() {
		// 获得系统中所有包
		packageInfos = getPackageManager().getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES | PackageManager.GET_ACTIVITIES);
		// 实例化用户自己安装的程序
		userPackageInfos = new ArrayList<PackageInfo>();
		for (PackageInfo temp : packageInfos) {
			boolean flag = false;
			ApplicationInfo appInfo = temp.applicationInfo;
			if ((appInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
				// 更新过的系统应用程序
				flag = true;
			} else if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				// 用户自己的应用程序
				flag = true;
			}
			if (flag) {
				userPackageInfos.add(temp);
			}
		}
		// 发送一个信息给主线程，让主线程把ProgressDialog给取消掉
		
		// 不同的操作就会有不同的参数值，该参数主要用来区分不同的操作
		//我们可以用这个值来对用户不同的操作进行区分
		
		try {// 为了看到演示效果，加上下面这句话
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		handler.sendEmptyMessage(SEARCH_APP);
		try {
			Thread.sleep(5000);
			handler.sendEmptyMessage(DELETE_APP);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void onClick(View v) {

		// TODO Auto-generated method stub
		if(v==changeCategoryBtn)
		{
			if(isAllApp) {
				changeCategoryBtn.setImageResource(R.drawable.user);
				//gv.setAdapter(new GridViewAdapter(ShowAppActivity.this,userPackageInfos));
				showPackageInfos = userPackageInfos;
				isAllApp = false;
				//Toast.makeText(ShowAppActivity.this, "用户安装的程序列表", Toast.LENGTH_SHORT).show();
				String userString=new String("用户安装的程序列表");
				
				MyToast.myToastShow(ShowApp.this, R.drawable.user, "用户安装的程序列表", Toast.LENGTH_SHORT);
			} else {
				changeCategoryBtn.setImageResource(R.drawable.all);
				//gv.setAdapter(new GridViewAdapter(ShowAppActivity.this,packageInfos));
				showPackageInfos = packageInfos;
				isAllApp = true;
				//Toast.makeText(ShowAppActivity.this, "所有程序列表", Toast.LENGTH_SHORT).show();
				MyToast.myToastShow(ShowApp.this, R.drawable.all, "所有程序列表", Toast.LENGTH_SHORT);
			}
			
			gridView.setAdapter(new GridViewAdapter(showPackageInfos,ShowApp.this));
			listView.setAdapter(new ListViewAdapter(showPackageInfos,ShowApp.this));
		}
		else if (v==changeViewBtn) {
			if(isListView) {
				//Toast.makeText(ShowAppActivity.this, "网格显示", Toast.LENGTH_SHORT).show();
				MyToast.myToastShow(ShowApp.this, R.drawable.grids, "网格显示", Toast.LENGTH_SHORT);
				changeViewBtn.setImageResource(R.drawable.grids);
				listView.setVisibility(View.GONE);
				gridView.setVisibility(View.VISIBLE);
				
				
				//AlphaAnimation 控制渐变透明的动画效果
				//ScaleAnimation 控制尺寸伸缩的动画效果
				//TranslateAnimation  控制画面平移的动画效果
				//RotateAnimation  控制画面角度变化的动画效果
				
				//LayoutAnimation  渲染ViewGroup中每个View显示时候的动画效果
				
				AnimationSet  set = new AnimationSet(false);
				Animation animation = new RotateAnimation(60, 0);
				animation.setInterpolator(ShowApp.this, android.R.anim.overshoot_interpolator);
				animation.setDuration(200);
				set.addAnimation(animation);
				animation = new AlphaAnimation(0, 1);
				animation.setDuration(100);
				set.addAnimation(animation);
				gridView.startAnimation(set);
				//Animation animation = AnimationUtils.loadAnimation(ShowAppActivity.this, R.anim.set1);
				//gv.startAnimation(set);
				gridView.startLayoutAnimation();
				
				
				isListView  = false;
			} else {
				//Toast.makeText(ShowAppActivity.this, "列表显示", Toast.LENGTH_SHORT).show();
				MyToast.myToastShow(ShowApp.this, R.drawable.list, "列表显示", Toast.LENGTH_SHORT);
				changeViewBtn.setImageResource(R.drawable.list);
				gridView.setVisibility(View.GONE);
				listView.setVisibility(View.VISIBLE);
				
				AnimationSet set = new AnimationSet(false);

				Animation animation = new TranslateAnimation(200, 1, 200, 1);
				animation.setDuration(100);
				animation.setInterpolator(ShowApp.this, android.R.anim.bounce_interpolator);
				set.addAnimation(animation);
				
				animation = new ScaleAnimation(0, 1, 0, 1);
				animation.setDuration(100);
				set.addAnimation(animation);
				//Animation animation = AnimationUtils.loadAnimation(ShowAppActivity.this, R.anim.set2);
				listView.startAnimation(set);
				isListView = true;
			}
			
		}
    	
   
		}
    	}
	
	
	
	
