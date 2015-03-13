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
		// ����Ϣ���͹�����ʱ���ִ�������������
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			if(msg.what == SEARCH_APP){
				showPackageInfos = packageInfos;
				gridView.setAdapter(new GridViewAdapter(showPackageInfos, ShowApp.this));
				listView.setAdapter(new ListViewAdapter(showPackageInfos, ShowApp.this));
				//���ñ�����������ɼ�
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
		//�б���ͼ��������ͼ�л�
		gridView.setOnItemClickListener(listener);
		listView.setOnItemClickListener(listener);
		 pd = ProgressDialog.show(this, "���Ժ�...", "��������������װ��Ӧ�ó���...",true,false);
		Thread thread = new Thread(this);
		thread.start();
		//���ñ���������ɼ�
		setProgressBarIndeterminateVisibility(true);
	}
	OnItemClickListener listener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			//ͨ��positionȡ����Ӧapk��packageInfo
			final PackageInfo packageInfo = showPackageInfos.get(position);
			//����һ��Dialog��������ѡ��
			Builder builder = new AlertDialog.Builder(ShowApp.this);
		      
			builder.setTitle("ѡ��");
			//����һ����Դ��ID
			builder.setItems(R.array.choice,new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case 0:
						String packageName = packageInfo.packageName;
						ActivityInfo activityInfo = packageInfo.activities[0];
						//activities����ֻ����������PackageManager.GET_ACTIVITIES��Żᱻ���
						//���ڻ�ȡpackageInfoʱҪ�ں������һ�����������
						if(activityInfo == null) {
							Toast.makeText(ShowApp.this, "û���κ�activity", Toast.LENGTH_SHORT).show();
							return;
						}
						String activityName = activityInfo.name;
						Intent intent = new Intent();
						//ͨ������������������Ӧ�ó���
						intent.setComponent(new ComponentName(packageName,activityName));
						//����apk
						startActivity(intent);
						break;
					case 1:
						//��ʾapk��ϸ��Ϣ
						showAppDetail(packageInfo);
						break;
					case 2:
						Uri packageUri = Uri.parse("package:" + packageInfo.packageName);
						Intent deleteIntent = new Intent();
						deleteIntent.setAction(Intent.ACTION_DELETE);
						deleteIntent.setData(packageUri);
						//������仰��Ϊ�ˣ����ɾ����Ӧ�ú󣬳���ͼ����Ȼ���ڵ�Bug���������onActivityResult����
						startActivityForResult(deleteIntent, 0);
						break;
					}
				}
			});
			//�˴���Ϊnull����ΪĬ�Ͼ�ʵ���˹رչ���
			builder.setNegativeButton("ȡ��", null);
			
			builder.create().show();
		}
	};
		@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//�������apk
		packageInfos = getPackageManager().getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES | PackageManager.GET_ACTIVITIES);
		userPackageInfos = new ArrayList<PackageInfo>();
		for(int i=0;i<packageInfos.size();i++) {
			
			PackageInfo temp = packageInfos.get(i);
			ApplicationInfo appInfo = temp.applicationInfo;
			boolean flag = false;
			if((appInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
				flag = true;
				//FLAG_SYSTEM������ϵͳapk
			} else if((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				// �û�apk
				flag = true;
			}
			if(flag) {
				//��ӵ�ϵͳapk������
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
	//��ʾapk����ϸ��Ϣ
	private void showAppDetail(PackageInfo packageInfo) {	
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("��ϸ��Ϣ");
		StringBuffer message = new StringBuffer();
		message.append("��������:" + packageInfo.applicationInfo.loadLabel(getPackageManager()));
		message.append("\n ����:" + packageInfo.packageName);//����
		message.append("\n �汾��:" + packageInfo.versionCode);//�汾��
		message.append("\n �汾��:" + packageInfo.versionName);//�汾��		
		builder.setMessage(message.toString());
		builder.setIcon(packageInfo.applicationInfo.loadIcon(getPackageManager()));
		builder.setPositiveButton("ȷ��", null);//��������Dialog��ʧ
		builder.create().show();
	}
		// ����¿��ٵ��߳���Ҫ������ListView����������Ա������������߳�
	@Override
	protected void onResume() {
		super.onResume();
	}
	@Override
	public void run() {
		// ���ϵͳ�����а�
		packageInfos = getPackageManager().getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES | PackageManager.GET_ACTIVITIES);
		// ʵ�����û��Լ���װ�ĳ���
		userPackageInfos = new ArrayList<PackageInfo>();
		for (PackageInfo temp : packageInfos) {
			boolean flag = false;
			ApplicationInfo appInfo = temp.applicationInfo;
			if ((appInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
				// ���¹���ϵͳӦ�ó���
				flag = true;
			} else if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				// �û��Լ���Ӧ�ó���
				flag = true;
			}
			if (flag) {
				userPackageInfos.add(temp);
			}
		}
		// ����һ����Ϣ�����̣߳������̰߳�ProgressDialog��ȡ����
		
		// ��ͬ�Ĳ����ͻ��в�ͬ�Ĳ���ֵ���ò�����Ҫ�������ֲ�ͬ�Ĳ���
		//���ǿ��������ֵ�����û���ͬ�Ĳ�����������
		
		try {// Ϊ�˿�����ʾЧ��������������仰
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
				//Toast.makeText(ShowAppActivity.this, "�û���װ�ĳ����б�", Toast.LENGTH_SHORT).show();
				String userString=new String("�û���װ�ĳ����б�");
				
				MyToast.myToastShow(ShowApp.this, R.drawable.user, "�û���װ�ĳ����б�", Toast.LENGTH_SHORT);
			} else {
				changeCategoryBtn.setImageResource(R.drawable.all);
				//gv.setAdapter(new GridViewAdapter(ShowAppActivity.this,packageInfos));
				showPackageInfos = packageInfos;
				isAllApp = true;
				//Toast.makeText(ShowAppActivity.this, "���г����б�", Toast.LENGTH_SHORT).show();
				MyToast.myToastShow(ShowApp.this, R.drawable.all, "���г����б�", Toast.LENGTH_SHORT);
			}
			
			gridView.setAdapter(new GridViewAdapter(showPackageInfos,ShowApp.this));
			listView.setAdapter(new ListViewAdapter(showPackageInfos,ShowApp.this));
		}
		else if (v==changeViewBtn) {
			if(isListView) {
				//Toast.makeText(ShowAppActivity.this, "������ʾ", Toast.LENGTH_SHORT).show();
				MyToast.myToastShow(ShowApp.this, R.drawable.grids, "������ʾ", Toast.LENGTH_SHORT);
				changeViewBtn.setImageResource(R.drawable.grids);
				listView.setVisibility(View.GONE);
				gridView.setVisibility(View.VISIBLE);
				
				
				//AlphaAnimation ���ƽ���͸���Ķ���Ч��
				//ScaleAnimation ���Ƴߴ������Ķ���Ч��
				//TranslateAnimation  ���ƻ���ƽ�ƵĶ���Ч��
				//RotateAnimation  ���ƻ���Ƕȱ仯�Ķ���Ч��
				
				//LayoutAnimation  ��ȾViewGroup��ÿ��View��ʾʱ��Ķ���Ч��
				
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
				//Toast.makeText(ShowAppActivity.this, "�б���ʾ", Toast.LENGTH_SHORT).show();
				MyToast.myToastShow(ShowApp.this, R.drawable.list, "�б���ʾ", Toast.LENGTH_SHORT);
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
	
	
	
	
