package bhg.sucks.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import bhg.sucks.R;
import bhg.sucks.model.Artifact;
import bhg.sucks.model.Bonus;
import bhg.sucks.model.SkillContainer;


public class ArtifactsAdapter extends RecyclerView.Adapter<ArtifactsAdapter.ViewHolder> {

    private final List<Artifact> artifacts = new ArrayList<>();
    private Context context;

    public ArtifactsAdapter(LiveData<List<Artifact>> liveArtifacts) {
        liveArtifacts.observeForever((items) -> {
            artifacts.clear();
            artifacts.addAll(items);

            notifyDataSetChanged();
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View artifactView = inflater.inflate(R.layout.item_artifact, parent, false);
        return new ViewHolder(artifactView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Artifact a = artifacts.get(position);

        holder.nameTextView.setText(a.getName());
        holder.categoryTextView.setText(a.getCategory().getText(context));

        for (int i = 0; i < a.getSkills().size(); i++) {
            SkillContainer sc = a.getSkills().get(i);

            holder.skillTextViews[i].setText(context.getString(sc.getSkill().getResId()));
            holder.levelTextViews[i].setText(context.getString(R.string.artifact_level, sc.getLevel()));
            holder.bonusTextViews[i].setText(toBonusText(sc));
        }
    }

    private String toBonusText(SkillContainer sc) {
        StringBuilder result = new StringBuilder();

        if (sc.getSkill().getBonus() == Bonus.POS) {
            result.append('+');
        } else {
            result.append('-');
        }

        result.append(sc.getBonus());
        result.append('%');
        return result.toString();
    }

    @Override
    public int getItemCount() {
        return artifacts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView nameTextView;
        public TextView categoryTextView;
        public TextView[] skillTextViews;
        public TextView[] levelTextViews;
        public TextView[] bonusTextViews;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.nameTextView = itemView.findViewById(R.id.artifact_name);
            this.categoryTextView = itemView.findViewById(R.id.artifact_category);

            this.skillTextViews = new TextView[]{
                    itemView.findViewById(R.id.artifact_skills1),
                    itemView.findViewById(R.id.artifact_skills2),
                    itemView.findViewById(R.id.artifact_skills3),
                    itemView.findViewById(R.id.artifact_skills4),
                    itemView.findViewById(R.id.artifact_skills5)
            };

            this.levelTextViews = new TextView[]{
                    itemView.findViewById(R.id.artifact_level1),
                    itemView.findViewById(R.id.artifact_level2),
                    itemView.findViewById(R.id.artifact_level3),
                    itemView.findViewById(R.id.artifact_level4),
                    itemView.findViewById(R.id.artifact_level5)
            };

            this.bonusTextViews = new TextView[]{
                    itemView.findViewById(R.id.artifact_bonus1),
                    itemView.findViewById(R.id.artifact_bonus2),
                    itemView.findViewById(R.id.artifact_bonus3),
                    itemView.findViewById(R.id.artifact_bonus4),
                    itemView.findViewById(R.id.artifact_bonus5)
            };
        }

    }
}
