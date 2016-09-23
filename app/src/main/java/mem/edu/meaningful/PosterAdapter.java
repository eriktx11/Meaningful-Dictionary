package mem.edu.meaningful;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

//import android.support.v7.widget.RecyclerView;

/**
 * Created by erikllerena on 4/14/16.
 */
public class PosterAdapter extends ArrayAdapter<GridItem> {

    private Context mContex;
    private int layoutResourceId;
    private ArrayList<GridItem> mGridData = new ArrayList<GridItem>();

    public PosterAdapter(Context mContex, int layoutResourceId, ArrayList<GridItem> mGridData){
        super(mContex, layoutResourceId, mGridData);

        this.mContex=mContex;
        this.layoutResourceId=layoutResourceId;
        this.mGridData=mGridData;

    }

    public void setGridData(ArrayList<GridItem> mGridData) {
        this.mGridData = mGridData;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        View row = convertView;
        ViewHolder holder;

        if (row==null){
            LayoutInflater inflater = ((Activity) mContex).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.imageView = (ImageView) row.findViewById(R.id.posterImg);
            row.setTag(holder);
                }
        else {
            holder = (ViewHolder) row.getTag();
        }

        GridItem item = mGridData.get(position);
        Picasso.with(mContex).load(item.getImage()).into(holder.imageView);

        return row;

    }

    static class ViewHolder{
        ImageView imageView;
    }

}






