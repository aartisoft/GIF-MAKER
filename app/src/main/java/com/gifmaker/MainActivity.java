package com.gifmaker;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.darsh.multipleimageselect.activities.AlbumSelectActivity;
import com.darsh.multipleimageselect.helpers.Constants;
import com.darsh.multipleimageselect.models.Image;
import com.gifmaker.Adapter.ViewPagerAdapter;
import com.gifmaker.CustomText.TextMediumBold;
import com.gifmaker.Fragment.SelectedPhotoFragment;
import com.gifmaker.Interface.OnDurationChangeListner;
import com.gifmaker.Interface.OnGifFinishListnerr;
import com.gifmaker.Model.Picture;
import com.gifmaker.Utility.BaseActivity;

import java.io.File;
import java.util.ArrayList;

import static com.gifmaker.ShowGifActivity.path;

public class MainActivity extends BaseActivity {

    public ViewPager pager;
    public SeekBar seekBarAll;
    public SeekBar seekbarDuration;
    public TextMediumBold txtDuration;
    public TextMediumBold txtDurationAll;
    public TextMediumBold txtLetsFun;
    public LinearLayout lnvCreateGIF;
    public LinearLayout lnvController;
    public LinearLayout lnvInstruction;
    public LinearLayout lnvCommonAll;


    public int pagerSize = 0;
    public ArrayList<Picture> mPicturesAll;
    public boolean isFirstTime = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initToolBar("VMG GIF MAKER");
        initView();
        initData();
        initClickListenr();

    }


    @Override
    public void initView() {
        pager = (ViewPager) findViewById(R.id.pager);
        seekbarDuration = (SeekBar) findViewById(R.id.seekbarDuration);
        seekBarAll = (SeekBar) findViewById(R.id.seekBarAll);
        txtDuration = (TextMediumBold) findViewById(R.id.txtDuration);
        txtDurationAll = (TextMediumBold) findViewById(R.id.txtDurationAll);
        lnvCreateGIF = (LinearLayout) findViewById(R.id.lnvCreateGIF);
        lnvController = (LinearLayout) findViewById(R.id.lnvController);
        lnvCommonAll = (LinearLayout) findViewById(R.id.lnvCommonAll);
        lnvInstruction = (LinearLayout) findViewById(R.id.lnvInstruction);

        applicationManager.displayBanner((Activity) mContext);

    }

    @Override
    public void initData() {
        mPicturesAll = new ArrayList<>();
        setCommonDuration();
        setIndividual();
        isPictureSelect();



    }

    @Override
    public void initClickListenr() {


        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (getGalleryPermisipon() == true) {
                    Intent intent = new Intent(mContext, AlbumSelectActivity.class);
                    intent.putExtra(Constants.INTENT_EXTRA_LIMIT, 30);
                    startActivityForResult(intent, Constants.REQUEST_CODE);
                } else {
                    ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10);
                }

            }
        });


        lnvCreateGIF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



            }
        });




        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


                if (mPicturesAll.size()!=0){
                    if (pager.getCurrentItem()==0){
                        lnvCommonAll.setVisibility(View.VISIBLE);
                    }else {
                        lnvCommonAll.setVisibility(View.INVISIBLE);

                    }


                    seekBarAll.setProgress(mPicturesAll.get(pager.getCurrentItem()).getDuration());
                    txtDurationAll.setText(mPicturesAll.get(pager.getCurrentItem()).getDuration() + "");

                    seekbarDuration.setProgress(mPicturesAll.get(pager.getCurrentItem()).getDuration());
                    txtDuration.setText(mPicturesAll.get(pager.getCurrentItem()).getDuration() + "");

                }


            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_CODE && resultCode == RESULT_OK && data != null) {

            ArrayList<Picture> mPictures = new ArrayList<>();

            ArrayList<Image> images = data.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES);
            for (int i = 0; i < images.size(); i++) {
                Picture picture = new Picture();
                picture.setPath(images.get(i).path);
                mPictures.add(picture);
            }
            this.mPicturesAll = mPictures;
            initPhotos(mPictures);
            isPictureSelect();
        }
    }


    public void initPhotos(final ArrayList<Picture> mPictures) {
        final ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        for (int i = 0; i < mPictures.size(); i++) {
            adapter.addFragment(new SelectedPhotoFragment(mPictures.get(i), i), "");
        }
        pager.setClipToPadding(false);
        pager.setPadding(120, 0, 120, 0);
        pager.setPageMargin(10);
        pager.setAdapter(adapter);
        pagerSize = mPictures.size();
    }


    public void setCommonDuration(){

        seekBarAll.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {


                int i = seekBar.getProgress();

                for (int j=0;j<mPicturesAll.size();j++){
                    Picture picture = mPicturesAll.get(j);
                    picture.setDuration(i);
                    mPicturesAll.set(j, picture);
                }
                seekbarDuration.setProgress(i);
                seekBarAll.setProgress(i);
                txtDurationAll.setText(i + "");
                txtDurationAll.setText(i + "");

            }
        });




    }


    public void setIndividual(){

        seekbarDuration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {


                int i = seekBar.getProgress();
                Picture picture = mPicturesAll.get(pager.getCurrentItem());
                picture.setDuration(i);
                mPicturesAll.set(pager.getCurrentItem(), picture);
                seekbarDuration.setProgress(i);
                txtDuration.setText(i + "");

            }
        });




    }
    public void isPictureSelect(){

        if (mPicturesAll.size()==0){
            lnvController.setVisibility(View.GONE);
            pager.setVisibility(View.GONE);
            lnvInstruction.setVisibility(View.VISIBLE);
            lnvCreateGIF.setBackgroundColor(mContext.getResources().getColor(R.color.gray));
            lnvCreateGIF.setEnabled(false);
        }else {
            pager.setVisibility(View.VISIBLE);
            lnvController.setVisibility(View.VISIBLE);
            lnvInstruction.setVisibility(View.GONE);
            lnvCreateGIF.setBackgroundColor(mContext.getResources().getColor(R.color.blue_theme));
            lnvCreateGIF.setEnabled(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        isPictureSelect();
    }



}
