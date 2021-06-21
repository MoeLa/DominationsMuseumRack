package bhg.sucks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import bhg.sucks.dao.AppDatabase;
import bhg.sucks.model.Artifact;
import bhg.sucks.model.Category;
import bhg.sucks.model.Skill;
import bhg.sucks.model.SkillContainer;
import bhg.sucks.ui.ArtifactsAdapter;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "bgh.sucks.MESSAGE";
    public static final String SCREENCAPTURE_DATA = "bhg.sucks.SCREENCAPTURE_DATA";

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Intent screenCaptureData;
    private final ActivityResultLauncher<Intent> mAskForDrawOverlayPermissionsContract = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (Settings.canDrawOverlays(this)) {
                    Log.v("Request permission", "Draw overlays -> SUCCESS");
                    startOverlay();
                } else {
                    Toast.makeText(this, "No permission to draw overlays granted", Toast.LENGTH_LONG).show();
                }
            }
    );
    private final ActivityResultLauncher<Intent> mScreenCaptureContract = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    this.screenCaptureData = result.getData();
                    startOverlay();
                } else {
                    Toast.makeText(this, "No permission to capture the screen granted", Toast.LENGTH_LONG).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar tb = findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(tb);

        RecyclerView rvArtifacts = findViewById(R.id.rv_artifacts);
        rvArtifacts.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        executorService.execute(() -> {
            LiveData<List<Artifact>> liveArtifacts = AppDatabase.getDatabase(this)
                    .artifactDao()
                    .loadAllArtifacts();

            rvArtifacts.post(() -> {
                ArtifactsAdapter adapter = new ArtifactsAdapter(liveArtifacts);
                rvArtifacts.setAdapter(adapter);
                rvArtifacts.setLayoutManager(new LinearLayoutManager(this));
            });
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.main_activity_action_start_overlay) {
            startOverlay();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void startOverlay() {
        if (screenCaptureData == null) {
            MediaProjectionManager mProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            mScreenCaptureContract.launch(mProjectionManager.createScreenCaptureIntent());
            return;
        }

        if (!Settings.canDrawOverlays(this)) {
            mAskForDrawOverlayPermissionsContract.launch(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())));
            return;
        }

        Intent overlayServiceIntent = new Intent(this, OverlayIconService.class);
        overlayServiceIntent.putExtra(SCREENCAPTURE_DATA, (Intent) screenCaptureData);

        startService(overlayServiceIntent);
    }

    public void addDummyArtifact(View view) {
        executorService.execute(() -> {
            Random r = new Random();

            long rowId = AppDatabase.getDatabase(this)
                    .artifactDao()
                    .insert(Artifact.builder()
                            .name("Test-Artifact")
                            .category(Category.values()[r.nextInt(Category.values().length)])
                            .skills(Lists.newArrayList(
                                    SkillContainer.builder()
                                            .skill(Skill.values()[r.nextInt(Skill.values().length)])
                                            .quality((r.nextInt(2) + 1) * 6)
                                            .level(r.nextInt(10) + 1)
                                            .build(),
                                    SkillContainer.builder()
                                            .skill(Skill.values()[r.nextInt(Skill.values().length)])
                                            .quality((r.nextInt(2) + 1) * 6)
                                            .level(r.nextInt(10) + 1)
                                            .build(),
                                    SkillContainer.builder()
                                            .skill(Skill.values()[r.nextInt(Skill.values().length)])
                                            .quality((r.nextInt(2) + 1) * 6)
                                            .level(r.nextInt(10) + 1)
                                            .build(),
                                    SkillContainer.builder()
                                            .skill(Skill.values()[r.nextInt(Skill.values().length)])
                                            .quality((r.nextInt(2) + 1) * 6)
                                            .level(r.nextInt(10) + 1)
                                            .build(),
                                    SkillContainer.builder()
                                            .skill(Skill.values()[r.nextInt(Skill.values().length)])
                                            .quality((r.nextInt(2) + 1) * 6)
                                            .level(r.nextInt(10) + 1)
                                            .build()))
                            .build());
            Log.v("MainActivity > addDummyArtifact", "Added artifact with id " + rowId);
        });
    }

    public void clearArtifacts(View view) {
        executorService.execute(() -> {
            AppDatabase.getDatabase(this)
                    .artifactDao()
                    .deleteAll();
            Log.v("MainActivity > clearArtifacts", "Cleared all artifacts");
        });
    }

}