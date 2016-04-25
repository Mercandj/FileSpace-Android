package com.mercandalli.android.apps.files.admin.notification;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.mercandalli.android.library.baselibrary.device.Device;

import java.util.ArrayList;
import java.util.List;

public class SendNotificationAdapter extends RecyclerView.Adapter<SendNotificationAdapter.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_DEVICE = 1;

    @NonNull
    private final List<Device> mDevices = new ArrayList<>();

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            return new HeaderViewHolder(new SendNotificationHeaderView(parent.getContext()));
        }
        return new DeviceViewHolder(new DeviceRow(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (holder instanceof DeviceViewHolder) {
            final DeviceViewHolder deviceViewHolder = (DeviceViewHolder) holder;
            deviceViewHolder.setDevice(mDevices.get(position - 1));
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_HEADER;
        }
        return VIEW_TYPE_DEVICE;
    }

    @Override
    public int getItemCount() {
        return mDevices.size() + 1;
    }

    /* package */ void setDeviceList(final List<Device> dev) {
        mDevices.clear();
        mDevices.addAll(dev);
        notifyDataSetChanged();
    }

    /* package */ static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(final View itemView) {
            super(itemView);
        }
    }

    private static class HeaderViewHolder extends ViewHolder {
        public HeaderViewHolder(final SendNotificationHeaderView sendNotificationHeaderView) {
            super(sendNotificationHeaderView);
        }
    }

    private static class DeviceViewHolder extends ViewHolder {
        private final DeviceRow mDeviceRow;

        public DeviceViewHolder(final DeviceRow deviceRow) {
            super(deviceRow);
            mDeviceRow = deviceRow;
        }

        public void setDevice(final Device device) {
            mDeviceRow.setDevice(device);
        }
    }
}
