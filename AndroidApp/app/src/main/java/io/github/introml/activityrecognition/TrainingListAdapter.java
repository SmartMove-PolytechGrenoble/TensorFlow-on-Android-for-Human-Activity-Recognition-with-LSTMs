package io.github.introml.activityrecognition;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import BusinessClass.Exercice;
import BusinessClass.Training;
import BusinessClass.TrainingAdapter;

/**
 * Created by anthony on 28/02/18.
 */

public class TrainingListAdapter extends ArrayAdapter<Training> {

    public TrainingListAdapter( Context context, List<Training> trainings) {
        super(context, 0,  trainings);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_training,parent, false);
        }

        TrainingListAdapter.TrainingViewHolder viewHolder = (TrainingListAdapter.TrainingViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new TrainingListAdapter.TrainingViewHolder();
            viewHolder.trainingName = (TextView) convertView.findViewById(R.id.trainingName2TextView);
            convertView.setTag(viewHolder);
        }

        //getItem(position) va récupérer l'item [position] de la List<Tweet> tweets
        Training training = getItem(position);
        viewHolder.trainingName.setText(training.getName());

        return convertView;
    }
    private class TrainingViewHolder {
        public TextView trainingName;
    }

}
