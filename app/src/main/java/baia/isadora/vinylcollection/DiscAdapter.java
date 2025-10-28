package baia.isadora.vinylcollection;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import baia.isadora.vinylcollection.model.Artist;
import baia.isadora.vinylcollection.model.Disc;
import baia.isadora.vinylcollection.persistency.DiscsDatabase;
import baia.isadora.vinylcollection.utils.UtilsLocalDate;

public class DiscAdapter extends BaseAdapter {
    private Context context;
    private List<Disc> listDiscs;
    private String[] conditions;
    private static class DiscHolder{
        public TextView textViewValueName;
        public TextView textViewValueArtist;
        public TextView textViewValueReleaseYear;
        public TextView textViewValueGenre;
        public TextView textViewValueCondition;
        public TextView textViewValueSpeed;
        public TextView textViewValueAlreadyHas;
        public TextView textViewValueAcquiredDate;
    }

    public DiscAdapter(List<Disc> listDiscs, Context context) {
        this.listDiscs = listDiscs;
        this.context = context;

        conditions = context.getResources().getStringArray(R.array.conditions);
    }

    @Override
    public int getCount() {
        return listDiscs.size();
    }

    @Override
    public Object getItem(int position) {
        return listDiscs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DiscHolder holder;
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.lines_disc_list, parent, false);

            holder = new DiscHolder();
            holder.textViewValueName = convertView.findViewById(R.id.textViewValueName);
            holder.textViewValueArtist = convertView.findViewById(R.id.textViewValueNationality);
            holder.textViewValueReleaseYear = convertView.findViewById(R.id.textViewValueYear);
            holder.textViewValueGenre = convertView.findViewById(R.id.textViewValueGenre);
            holder.textViewValueCondition = convertView.findViewById(R.id.textViewValueCondition);
            holder.textViewValueSpeed = convertView.findViewById(R.id.textViewValueSpeed);
            holder.textViewValueAlreadyHas = convertView.findViewById(R.id.textViewValueAlreadyHas);
            holder.textViewValueAcquiredDate = convertView.findViewById(R.id.textViewValueOwnedSince);

            convertView.setTag(holder);
        } else {
            holder = (DiscHolder)convertView.getTag();
        }

        Disc disc = listDiscs.get(position);
        holder.textViewValueName.setText(disc.getName());
        DiscsDatabase database = DiscsDatabase.getInstance(context);
        long artistId = disc.getArtistId();
        Artist artist = database.getArtistDao().queryForId(artistId);

        if (artist != null) {
            holder.textViewValueArtist.setText(artist.getName());
        } else {
            holder.textViewValueArtist.setText(R.string.unknown_artist);
        }
        holder.textViewValueReleaseYear.setText(String.valueOf(disc.getReleaseYear()));
        holder.textViewValueGenre.setText(disc.getGenre());
        holder.textViewValueCondition.setText(conditions[disc.getCondition()]);
        switch(disc.getDiscSpeed()){
            case RPM_33:
                holder.textViewValueSpeed.setText(R.string.speed_33rpm);
                break;

            case RPM_45:
                holder.textViewValueSpeed.setText(R.string.speed_45rpm);
                break;
        }
        if(disc.isAlreadyHave()){
            holder.textViewValueAlreadyHas.setText(R.string.already_has_vinyl);
        } else {
            holder.textViewValueAlreadyHas.setText(R.string.dont_have_vinyl);
        }
        if(disc.getAcquiredDate() != null){
            holder.textViewValueAcquiredDate.setVisibility(View.VISIBLE);
            holder.textViewValueAcquiredDate.setText(UtilsLocalDate.formatLocalDate(disc.getAcquiredDate()));
        } else {
            holder.textViewValueAcquiredDate.setVisibility(View.GONE);
        }

        return convertView;
    }
}
