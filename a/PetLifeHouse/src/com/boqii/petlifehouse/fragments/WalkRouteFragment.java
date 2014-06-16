package com.boqii.petlifehouse.fragments;

import java.util.ArrayList;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.RouteSearch.OnRouteSearchListener;
import com.amap.api.services.route.RouteSearch.WalkRouteQuery;
import com.amap.api.services.route.WalkRouteResult;
import com.amap.api.services.route.WalkStep;
import com.boqii.petlifehouse.R;
import com.boqii.petlifehouse.activities.MapRouteActivity;
import com.boqii.petlifehouse.baseactivities.BaseFragment;
import com.boqii.petlifehouse.entities.Merchant;

public class WalkRouteFragment extends BaseFragment implements
		OnRouteSearchListener, OnItemClickListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	private View view;
	private ListView mWalkList;
	private Intent mIntent;
	private Merchant merchant;// 商户信息集合
	private RouteSearch routeSearch;// 路线查询
	private int walkMode = RouteSearch.WalkDefault;// 步行默认模式
	private LatLonPoint startLatLng;
	private LatLonPoint endLatLng;
	// private WalkRouteResult walkRouteResult;// 步行模式查询结果
	private WalkRouteAdaper walkAdaper;
	private TextView displayLocation;
	private ProgressBar mBar;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.route_walk, container, false);
		init();
		return view;
	}

	private void init() {
		displayLocation = (TextView) view.findViewById(R.id.walk_mylocation);
		mBar=(ProgressBar) view.findViewById(R.id.progressMW);
		mWalkList = (ListView) view.findViewById(R.id.walkRouteList);
		mWalkList.setOnItemClickListener(this);

		routeSearch = new RouteSearch(getActivity());
		routeSearch.setRouteSearchListener(this);

		mIntent = getActivity().getIntent();
		// 得到商户信息
		Bundle mBundle = mIntent.getBundleExtra("GoBundle");
		merchant = (Merchant) mBundle.getSerializable("Merchant");
		// 得到我的位置信息
		double[] location = mBundle.getDoubleArray("MyLocation");
		startLatLng = new LatLonPoint(location[0], location[1]);
		endLatLng = new LatLonPoint(merchant.MerchantLat, merchant.MerchantLng);
		searchRouteResult(startLatLng, endLatLng);
	}

	/**
	 * 开始搜索路径规划方案
	 */
	public void searchRouteResult(LatLonPoint startPoint, LatLonPoint endPoint) {

		final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
				startPoint, endPoint);
		WalkRouteQuery query = new WalkRouteQuery(fromAndTo, walkMode);
		routeSearch.calculateWalkRouteAsyn(query);// 异步路径规划步行模式查询

	}

	// 路线查询监听
	@Override
	public void onBusRouteSearched(BusRouteResult arg0, int arg1) {
	}

	@Override
	public void onDriveRouteSearched(DriveRouteResult arg0, int arg1) {
	}

	@Override
	public void onWalkRouteSearched(WalkRouteResult result, int rCode) {
		try {

			if (rCode == 0) {
				if (result != null && result.getPaths() != null
						&& result.getPaths().size() > 0) {
					// walkRouteResult = result;
					// WalkPath walkPath = walkRouteResult.getPaths().get(0);
					// aMap.clear();// 清理地图上的所有覆盖物
					// WalkRouteOverlay walkRouteOverlay = new
					// WalkRouteOverlay(this,
					// aMap, walkPath, walkRouteResult.getStartPos(),
					// walkRouteResult.getTargetPos());
					// walkRouteOverlay.removeFromMap();
					// walkRouteOverlay.addToMap();
					// walkRouteOverlay.zoomToSpan();
					walkAdaper = new WalkRouteAdaper(result);
					mWalkList.setAdapter(walkAdaper);
				} else {
					displayLocation.setText(getString(R.string.no_route));
					Toast.makeText(getActivity(), R.string.no_result,
							Toast.LENGTH_SHORT).show();
				}
			} else if (rCode == 27) {
				Toast.makeText(getActivity(), R.string.error_network,
						Toast.LENGTH_SHORT).show();
			} else if (rCode == 32) {
				Toast.makeText(getActivity(), R.string.error_key,
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getActivity(), R.string.error_other + rCode,
						Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mBar.setVisibility(Gallery.INVISIBLE);
		}
	}

	class WalkRouteAdaper extends BaseAdapter {
		public ArrayList<WalkPath> pathLst;
		public WalkRouteResult result;

		public WalkRouteAdaper(WalkRouteResult result) {
			this.result = result;
			pathLst = (ArrayList<WalkPath>) result.getPaths();
			//System.out.println(pathLst.size() + ".....pathLst.size");
		}

		@Override
		public int getCount() {
			return pathLst.size();
		}

		@Override
		public Object getItem(int position) {
			return pathLst.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			viewHolder holder = new viewHolder();
			if (convertView == null) {
				convertView = LayoutInflater.from(getActivity()).inflate(
						R.layout.route_list_item, null);
				holder.routeTxt = (TextView) convertView
						.findViewById(R.id.routeTxt);
				holder.routeTimeTxt = (TextView) convertView
						.findViewById(R.id.routeTimeTxt);
				holder.routeJourneyTxt = (TextView) convertView
						.findViewById(R.id.routeJourneyTxt);
				convertView.setTag(holder);
			} else {
				holder = (viewHolder) convertView.getTag();
			}

			// 路段
			StringBuffer buffer = new StringBuffer();
			ArrayList<WalkStep> step = (ArrayList<WalkStep>) pathLst.get(
					position).getSteps();
			String route="";
			for (WalkStep walkStep : step) {
				buffer.append(walkStep.getRoad() + "-");
				if(!walkStep.getRoad().equals("")){
					route=walkStep.getRoad();
				}
			}
			displayLocation.setText(route);
			// System.out.println(buffer);
			holder.routeTxt.setText(buffer);
			// 时间和路程
			int time = (int) (pathLst.get(position).getDuration() / 60);
			int journey = (int) (pathLst.get(position).getDistance() / 1000);
			holder.routeJourneyTxt.setText(journey
					+ getString(R.string.route_Journey));
			holder.routeTimeTxt.setText(time + getString(R.string.route_time));
			return convertView;
		}

		public class viewHolder {
			TextView routeTxt;// 路线
			TextView routeTimeTxt;// 所需时间
			TextView routeJourneyTxt;// 路程
		}

		public ArrayList<WalkPath> getPathLst() {
			return pathLst;
		}

		public WalkRouteResult getRouteResult() {
			return result;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> adapers, View view, int index,
			long arg3) {
		Intent mIntent = new Intent();
		mIntent.setClass(getActivity(), MapRouteActivity.class);
		mIntent.putExtra("RouteMode", WALK_MODE);
		ArrayList<LatLonPoint> fromTo = new ArrayList<LatLonPoint>();
		fromTo.add(startLatLng);
		fromTo.add(endLatLng);
		mIntent.putExtra("FromAndTo", fromTo);
		startActivity(mIntent);
	}

	public static final int WALK_MODE = 1;
}
