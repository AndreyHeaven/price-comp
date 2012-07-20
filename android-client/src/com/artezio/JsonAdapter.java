package com.artezio;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: araigorodskiy
 * Date: 7/20/12
 * Time: 4:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class JsonAdapter extends BaseAdapter {
    //    private Context context;
    private JSONArray data;
    private int resource;
    private String[] from;
    private int[] to;
    private final LayoutInflater mInflater;


    public JsonAdapter(Context context, JSONArray data, int resource, String[] from, int[] to) {
//        this.context = context;
        this.data = data;
        this.resource = resource;
        this.from = from;
        this.to = to;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public JsonAdapter(Context context, int resource, String[] from, int[] to) {
//        this.context = context;
        this.resource = resource;
        this.from = from;
        this.to = to;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data != null ? data.length() : 0;
    }

    public void setData(JSONArray data){
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public Object getItem(int i) {
        try {
            return data != null ? data.get(i) : null;
        } catch (JSONException e) {
            return null;
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(position, convertView, parent, resource);
    }

    private View createViewFromResource(int position, View convertView,
                                        ViewGroup parent, int resource) {
        View v;
        if (convertView == null) {
            v = mInflater.inflate(resource, parent, false);

            final int[] to = this.to;
            final int count = to.length;
            final View[] holder = new View[count];

            for (int i = 0; i < count; i++) {
                holder[i] = v.findViewById(to[i]);
            }

            v.setTag(holder);
        } else {
            v = convertView;
        }

        bindView(position, v);

        return v;
    }

    private void bindView(int position, View view) {
        JSONObject dataSet;
        try {
            dataSet = data.getJSONObject(position);
        } catch (JSONException e) {
            dataSet = null;
        }
        if (dataSet == null) {
            return;
        }

//        final SimpleAdapter.ViewBinder binder = mViewBinder;
        final View[] holder = (View[]) view.getTag();
        final String[] from = this.from;
        final int[] to = this.to;
        final int count = to.length;

        for (int i = 0; i < count; i++) {
            final View v = holder[i];
            if (v != null) {
                final String data;
                String text = null;
                try {
                    data = dataSet.getString(from[i]);
                    text = data == null ? "" : data;
                } catch (JSONException e) {
                    text = null;
                }

                if (text == null) {
                    text = "";
                }

                boolean bound = false;
//                if (binder != null) {
//                    bound = binder.setViewValue(v, data, text);
//                }

//                if (!bound) {
/*
                    if (v instanceof Checkable) {
                        if (data instanceof Boolean) {
                            ((Checkable) v).setChecked((Boolean) data);
                        } else {
                            throw new IllegalStateException(v.getClass().getName() +
                                    " should be bound to a Boolean, not a " + data.getClass());
                        }
                    } else
*/
                if (v instanceof TextView) {
                    // Note: keep the instanceof TextView check at the bottom of these
                    // ifs since a lot of views are TextViews (e.g. CheckBoxes).
                    setViewText((TextView) v, text);
                }
/*
                    else if (v instanceof ImageView) {
                        if (data instanceof Integer) {
                            setViewImage((ImageView) v, (Integer) data);
                        } else {
                            setViewImage((ImageView) v, text);
                        }
*/
            } else {
                throw new IllegalStateException(v.getClass().getName() + " is not a " +
                        " view that can be bounds by this SimpleAdapter");
            }
//                }
        }
//        }
    }

    public void setViewText(TextView v, String text) {
        v.setText(text);
    }
}
