package com.example.ardemo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;

import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.net.URI;

/**
 * TODO: Created by Sananda Banik on 17-01-2020
 */
public class MainActivity extends AppCompatActivity {

    //reference to the scenic plane
    private ArFragment arFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
        arFragment.setOnTapArPlaneListener((HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {

            /**
             * Describes a fix location and orientation in the real world
             */
            Anchor anchor = hitResult.createAnchor();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                ModelRenderable.builder()
                        .setSource(this, Uri.parse("Butterfly.sfb"))
                        .build()
                        .thenAccept(modelRenderable -> {
                            addModelToScene(anchor, modelRenderable);
                        })
                        .exceptionally(throwable -> {
                            AlertDialog.Builder alert = new AlertDialog.Builder(this);
                            alert.setMessage(throwable.getMessage());
                            return null;
                        });
            }

        });
    }

    private void addModelToScene(Anchor anchor, ModelRenderable modelRenderable) {
        //positions itself on the real world based on the anchor, unable to move,scale,zoom
        AnchorNode anchorNode = new AnchorNode(anchor);
        //modelRenderable.getMaterial().setFloat4("baseColorTint",new Color(1.0f,0.0f,0.0f,1.1f));

        //enables anchorNode to move,scale,zoom
        TransformableNode transformableNode = new TransformableNode(arFragment.getTransformationSystem());
        //set the anchorNode as the parent of the transformableNode
        transformableNode.setParent(anchorNode);
        //renders the 3d model
        transformableNode.setRenderable(modelRenderable);

        //set the 3d model/.sfb on top of the scenic platform
        arFragment.getArSceneView().getScene().addChild(anchorNode);
        transformableNode.select();
    }
}
