package BusinessClass;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import io.github.introml.activityrecognition.R;

/**
 * Created by anthony on 08/02/18.
 */

public class TrainingAdapter extends ArrayAdapter<Exercice>{

    public TrainingAdapter(Context context, Training training){
        super(context,0, training.getExercices());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_exercice,parent, false);
        }

        ExerciceViewHolder viewHolder = (ExerciceViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new ExerciceViewHolder();
            viewHolder.repetion = (TextView) convertView.findViewById(R.id.repetitionTextView);
            viewHolder.movement = (TextView) convertView.findViewById(R.id.movementTextView);
            convertView.setTag(viewHolder);
        }

        //getItem(position) va récupérer l'item [position] de la List<Tweet> tweets
        Exercice exercice = getItem(position);
        viewHolder.repetion.setText(String.valueOf(exercice.getRepetition()));
        viewHolder.movement.setText(exercice.getMovement().toString());

        return convertView;
    }

    private class ExerciceViewHolder {
        public TextView repetion;
        public TextView movement;


    }
}
