package com.olathonxa.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.olathonxa.R;
import com.olathonxa.model.AddMember;

public class AddMemberAdapter extends BaseAdapter {
	
	Context context;
	ArrayList<AddMember> memberList;
	
	public AddMemberAdapter(Context context, ArrayList<AddMember> userIdList){
		this.context = context;
		this.memberList = userIdList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return memberList.size();
	}

	@Override
	public Object getItem(int pos) {
		return memberList.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final int pos = position;
		ViewHolder viewHolder = null;
		if (convertView == null) {
			LayoutInflater inflator = ((Activity) context).getLayoutInflater();
			convertView = inflator.inflate(R.layout.list_item_add_member,
					null);
			viewHolder = new ViewHolder();
			viewHolder.imgRemove = (ImageView) convertView
					.findViewById(R.id.imageViewRemove);
			viewHolder.txtUser = (TextView) convertView
					.findViewById(R.id.textViewUser);
			
			convertView.setTag(viewHolder);

		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
	
		 viewHolder.txtUser.setText(memberList.get(position).getUserId());
		 viewHolder.imgRemove.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// Remove Id
			}
		});
	        
		return convertView;
	}
	
	public class ViewHolder {
		ImageView imgRemove;
		TextView txtUser;
	}

}
