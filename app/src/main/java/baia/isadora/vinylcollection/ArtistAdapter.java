package baia.isadora.vinylcollection;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import baia.isadora.vinylcollection.model.Artist;

public class ArtistAdapter extends BaseAdapter {
    private Context context;
    private List<Artist> listArtists;
    private String[] conditions;
    private static class ArtistHolder{
        public TextView textViewValueName;
        public TextView textViewValueNationality;
    }

    public ArtistAdapter(List<Artist> listArtists, Context context) {
        this.listArtists = listArtists;
        this.context = context;

        conditions = context.getResources().getStringArray(R.array.conditions);
    }

    @Override
    public int getCount() {
        return listArtists.size();
    }

    @Override
    public Object getItem(int position) {
        return listArtists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ArtistAdapter.ArtistHolder holder;
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.lines_artist_list, parent, false);

            holder = new ArtistAdapter.ArtistHolder();
            holder.textViewValueName = convertView.findViewById(R.id.textViewValueName);
            holder.textViewValueNationality = convertView.findViewById(R.id.textViewValueNationality);

            convertView.setTag(holder);
        } else {
            holder = (ArtistAdapter.ArtistHolder)convertView.getTag();
        }

        Artist artist = listArtists.get(position);
        holder.textViewValueName.setText(artist.getName());
        holder.textViewValueNationality.setText(artist.getNationality());

        return convertView;
    }
}
