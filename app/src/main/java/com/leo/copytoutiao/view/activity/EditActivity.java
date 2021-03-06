package com.leo.copytoutiao.view.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Picture;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bigkoo.pickerview.TimePickerView;
import com.leo.copytoutiao.R;
import com.leo.copytoutiao.databinding.ActivityPublishBinding;
import com.leo.copytoutiao.model.bean.NoteBean;
import com.leo.copytoutiao.model.repository.LoginRepository;
import com.leo.copytoutiao.service.AlarmService;
import com.leo.copytoutiao.utils.AlarmManagerUtil;
import com.leo.copytoutiao.utils.Color;
import com.leo.copytoutiao.utils.HtmlUtil;
import com.leo.copytoutiao.utils.KeyBoardUtils;
import com.leo.copytoutiao.utils.RichUtils;
import com.leo.copytoutiao.utils.Utils;
import com.leo.copytoutiao.utils.popup.CommonPopupWindow;
import com.leo.copytoutiao.view.RichEditor;
import com.leo.copytoutiao.viewmodel.NoteViewModel;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.taoke.base.BaseActivity;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yalantis.ucrop.UCropActivity;

import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import cn.leancloud.AVFile;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static com.yalantis.ucrop.UCrop.EXTRA_OUTPUT_URI;

/**
 * ?????????????????????
 */
public class EditActivity extends BaseActivity implements View.OnClickListener {
    ActivityPublishBinding binding;
    RxPermissions rxPermissions;
    private ArrayList<ImageItem> selectImages = new ArrayList<>();

    private CommonPopupWindow popupWindow; //???????????????pop
    private String currentUrl = "";
    private static final String TAG = "EditActivity";

    //??????item??????????????????????????????????????????????????????????????????
    private boolean mIsItalicSelect = false;
    private int mThreshold;
    private int mCurProgress = 25; //seekBar?????????
    private NoteBean mNote;
    private NoteViewModel mViewModel;
    public static final int CREATE_NOTE = 3239; //????????????
    public static final int Edit_NOTE = 8374; //????????????
    private int mPosition; //???????????????????????????,-1????????????????????????
    private TimePickerView timePicker;
    private ProgressDialog dialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_publish);
        mNote = (NoteBean) getIntent().getSerializableExtra("note");
        mPosition = getIntent().getIntExtra("position", -1);
        binding.setOnClickListener(this);
        rxPermissions = new RxPermissions(this);
        mViewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(NoteViewModel.class);
        initPop();
        initEditor();
        //initThreshold();
        initViews();
        initToolBar(findViewById(R.id.toolbar), "??????", true, -1);
    }

    public static void startActivityForResult(Fragment fragment, NoteBean note, int position) {
        Intent intent = new Intent(fragment.getContext(), EditActivity.class);
        intent.putExtra("note", note);
        intent.putExtra("position", position);
        fragment.startActivityForResult(intent,Edit_NOTE);
    }

    public static void startActivityForResult(Fragment fragment, String kind){
        NoteBean note = new NoteBean(null, null, null, kind, 0, LoginRepository.getInstance(fragment.getActivity().getApplicationContext()).getCurrentUser(), 0, UUID.randomUUID().toString());
        Intent intent = new Intent(fragment.getContext(), EditActivity.class);
        intent.putExtra("note", note);
        fragment.startActivityForResult(intent,CREATE_NOTE);
    }

    public static void startActivityForResult(AppCompatActivity context, NoteBean note, int position){
        Intent intent = new Intent(context, EditActivity.class);
        intent.putExtra("note", note);
        intent.putExtra("position", position);
        context.startActivityForResult(intent, Edit_NOTE);
    }


    private void initEditor() {
        //??????????????????????????????
        binding.richEditor.setEditorFontSize(18);
        //??????????????????????????????
        binding.richEditor.setEditorFontColor(getResources().getColor(R.color.black));
        //?????????????????????
        binding.richEditor.setEditorBackgroundColor(Color.WHITE);
        //???????????????padding
        binding.richEditor.setPadding(10, 10, 10, 10);
        //??????????????????
        binding.richEditor.setPlaceholder("????????????????????????~");

        //???????????????????????????
        binding.richEditor.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
            @Override
            public void onTextChange(String text) {
                Log.e("?????????????????????", text);
//                if (TextUtils.isEmpty(binding.editName.getText().toString().trim())) {
//                    binding.txtPublish.setSelected(false);
//                    binding.txtPublish.setEnabled(false);
//                    return;
//                }
//
//                if (TextUtils.isEmpty(text)) {
//                    binding.txtPublish.setSelected(false);
//                    binding.txtPublish.setEnabled(false);
//                } else {
//
//                    if (TextUtils.isEmpty(Html.fromHtml(text))) {
//                        binding.txtPublish.setSelected(false);
//                        binding.txtPublish.setEnabled(false);
//                    } else {
//                        binding.txtPublish.setSelected(true);
//                        binding.txtPublish.setEnabled(true);
//                    }
//
//                }

            }
        });

        binding.editName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
//                String html = binding.richEditor.getHtml();
//                if (TextUtils.isEmpty(html)) {
//                    binding.txtPublish.setSelected(false);
//                    binding.txtPublish.setEnabled(false);
//                    return;
//                } else {
//                    if (TextUtils.isEmpty(Html.fromHtml(html))) {
//                        binding.txtPublish.setSelected(false);
//                        binding.txtPublish.setEnabled(false);
//                        return;
//                    } else {
//                        binding.txtPublish.setSelected(true);
//                        binding.txtPublish.setEnabled(true);
//                    }
//                }
//
//                if (TextUtils.isEmpty(s.toString())) {
//                    binding.txtPublish.setSelected(false);
//                    binding.txtPublish.setEnabled(false);
//                } else {
//                    binding.txtPublish.setSelected(true);
//                    binding.txtPublish.setEnabled(true);
//                }


            }
        });

        binding.richEditor.setOnDecorationChangeListener(new RichEditor.OnDecorationStateListener() {
            @Override
            public void onStateChangeListener(String text, List<RichEditor.Type> types) {
                ArrayList<String> flagArr = new ArrayList<>();
                for (int i = 0; i < types.size(); i++) {
                    flagArr.add(types.get(i).name());
                }
                Log.d("sakura", flagArr.toString());

                if (flagArr.contains("BOLD")) {
                    binding.buttonBold.setImageResource(R.mipmap.bold_);
                } else {
                    binding.buttonBold.setImageResource(R.mipmap.bold);
                }

                if (flagArr.contains("UNDERLINE")) {
                    binding.buttonUnderline.setImageResource(R.mipmap.underline_);
                } else {
                    binding.buttonUnderline.setImageResource(R.mipmap.underline);
                }

                if (flagArr.contains("ITALIC")) {
                    binding.buttonItalic.setImageResource(R.mipmap.icon_italic_selected);
                } else {
                    binding.buttonItalic.setImageResource(R.mipmap.icon_italic_no_select);
                }


                if (flagArr.contains("ORDEREDLIST")) {
                    binding.buttonListOl.setImageResource(R.mipmap.list_ol_);
                } else {
                    binding.buttonListOl.setImageResource(R.mipmap.list_ol);
                }

                if (flagArr.contains("UNORDEREDLIST")) {
                    binding.buttonListUl.setImageResource(R.mipmap.list_ul_);
                } else {
                    binding.buttonListUl.setImageResource(R.mipmap.list_ul);
                }


            }
        });
        binding.richEditor.setOnInitialLoadListener(new RichEditor.AfterInitialLoadListener() {
            @Override
            public void onAfterInitialLoad(boolean isReady) {
                binding.richEditor.setFontSize(4);
            }
        });


        binding.richEditor.setImageClickListener(new RichEditor.ImageClickListener() {
            @Override
            public void onImageClick(String imageUrl) {
                currentUrl = imageUrl;
                popupWindow.showBottom(binding.getRoot(), 0.5f);
            }
        });


    }


    private void initPop() {
        View view = LayoutInflater.from(EditActivity.this).inflate(R.layout.newapp_pop_picture, null);
        view.findViewById(R.id.linear_cancle).setOnClickListener(v -> {
            popupWindow.dismiss();
        });

//        view.findViewById(R.id.linear_editor).setOnClickListener(v -> {
//            //????????????
//
//            rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE).subscribe(aBoolean -> {
//                if (aBoolean) {
//                    if (ActivityCompat.checkSelfPermission(EditActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(EditActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//
//                        Intent intent = new Intent(EditActivity.this, UCropActivity.class);
//                        intent.putExtra("filePath", currentUrl);
//                        String destDir = getFilesDir().getAbsolutePath().toString();
//                        String fileName = "SampleCropImage" + System.currentTimeMillis() + ".jpg";
//                        intent.putExtra("outPath", destDir + fileName);
//                        startActivityForResult(intent, 11);
//                        popupWindow.dismiss();
//
//                    }
//                } else {
//                    Toast.makeText(EditActivity.this, "?????????????????????", Toast.LENGTH_SHORT).show();
//                }
//            });
//        });

        view.findViewById(R.id.linear_delete_pic).setOnClickListener(v -> {
            //????????????

            String removeUrl = "<img src=\"" + currentUrl + "\" alt=\"dachshund\" width=\"100%\"><br>";

            String newUrl = binding.richEditor.getHtml().replace(removeUrl, "");
            currentUrl = "";
            binding.richEditor.setHtml(newUrl);
            if (RichUtils.isEmpty(binding.richEditor.getHtml())) {
                binding.richEditor.setHtml("");
            }
            popupWindow.dismiss();
        });
        popupWindow = new CommonPopupWindow.Builder(EditActivity.this)
                .setView(view)
                .setWidthAndHeight(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                .setOutsideTouchable(true)//???????????????????????????
                .setAnimationStyle(R.style.pop_animation)//??????popWindow???????????????
                .create();


        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                binding.richEditor.setInputEnabled(true);
            }
        });
    }

    private void initThreshold() {
        //??????layout?????????????????????????????????
        binding.container.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        SharedPreferences sharedPreferences = getSharedPreferences("art", MODE_PRIVATE);
                        SharedPreferences.Editor edit = sharedPreferences.edit();
                        mThreshold = sharedPreferences.getInt("height", 0);
                        if (mThreshold == 0) {
                            edit.putInt("height", binding.container.getHeight());
                            edit.apply();
                        }
                        if (binding.container.getHeight() < mThreshold) {
                            // ???????????????????????????
                            binding.richEditor.focusEditor();
                            binding.fontContainer.setVisibility(View.GONE);
                            binding.editorContainer.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    private void initViews() {
        binding.folderName.setText(mNote.getKind());
        binding.editName.setText(mNote.getTitle());
        binding.richEditor.setHtml(mNote.getContent());
        //?????????????????????
        binding.seekbar.setMax(100);
        binding.seekbar.setProgress(25);
        binding.seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mCurProgress = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int size = Math.min(mCurProgress / 25 + 1, 4);
                seekBar.setProgress(size * 25);
                //???????????????????????????
                binding.richEditor.setFontSize(size + 3);
                dismissFont();
            }
        });

        //????????????
        Calendar selectedDate = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        startDate.set(2020, 0, 1);
        Calendar endDate = Calendar.getInstance();
        endDate.set(2040, 11, 31);
        timePicker = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//??????????????????
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                AlarmManagerUtil util = AlarmManagerUtil.getInstance(EditActivity.this);
                Intent intent = new Intent(EditActivity.this, AlarmService.class);
                //???????????????????????????Note?????????AlarmService????????????null
                //????????????????????????,????????????title???content
                //intent.putExtra("alarmNote", mNote);
                intent.putExtra("id",mNote.getId());
                intent.putExtra("title",mNote.getTitle());
                intent.putExtra("content",HtmlUtil.getTextFromHtml(mNote.getContent()));
                PendingIntent pendingIntent = PendingIntent.getService(EditActivity.this,0, intent, 0);
                util.addAlarm(calendar, pendingIntent);
                Toast.makeText(EditActivity.this,"??????????????????",Toast.LENGTH_SHORT).show();
                //???????????????
                mViewModel.saveAlarmTime(mNote, date.getTime());
            }
        }).setType(new boolean[]{true, true, true, true, true, false})
                .setLabel(" ???", "???", "???", "???", "", "")
                .isCenterLabel(true)
                .setDividerColor(android.graphics.Color.DKGRAY)
                .setContentSize(20)
                .setDate(selectedDate)
                .setRangDate(startDate, endDate)
                .setDecorView(null)
                .build();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_bold:
                //??????
                againEdit();
                binding.richEditor.setBold();
                break;

            case R.id.button_underline:
                //????????????
                againEdit();
                binding.richEditor.setUnderline();
                break;

            case R.id.button_list_ul:
                //?????????????????????
                againEdit();
                binding.richEditor.setBullets();
                break;

            case R.id.button_list_ol:
                //????????????????????????
                againEdit();
                binding.richEditor.setNumbers();
                break;
            case R.id.button_italic:
                //??????
                againEdit();
                binding.richEditor.setItalic();
                if (mIsItalicSelect) {
                    binding.buttonItalic.setImageResource(R.mipmap.icon_italic_no_select);
                } else {
                    binding.buttonItalic.setImageResource(R.mipmap.icon_italic_selected);
                }
                mIsItalicSelect = !mIsItalicSelect;
                break;
            case R.id.button_image:
                if (!TextUtils.isEmpty(binding.richEditor.getHtml())) {
                    ArrayList<String> arrayList = RichUtils.returnImageUrlsFromHtml(binding.richEditor.getHtml());
                    if (arrayList != null && arrayList.size() >= 9) {
                        Toast.makeText(EditActivity.this, "????????????9?????????~", Toast.LENGTH_SHORT).show();
                        return;
                    }

                }

                rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE).subscribe(aBoolean -> {
                    if (aBoolean) {
                        if (ActivityCompat.checkSelfPermission(EditActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(EditActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            selectImage(104, selectImages);
                            KeyBoardUtils.closeKeybord(binding.editName, EditActivity.this);
                        }
                    } else {
                        Toast.makeText(EditActivity.this, "?????????????????????~", Toast.LENGTH_SHORT).show();

                    }
                });
                break;
            //??????????????????????????????????????????????????????????????????????????????????????????
            case R.id.button_font:
                binding.fontContainer.setVisibility(View.VISIBLE);
                binding.editorContainer.setVisibility(View.GONE);
                KeyBoardUtils.closeKeybord(binding.editName, EditActivity.this);
                break;

            //??????????????????????????????
            case R.id.font_cancel:
                dismissFont();
                break;
            case R.id.iv_indent:
                binding.richEditor.setIndent();
                dismissFont();
                break;
            case R.id.iv_outdent:
                binding.richEditor.setOutdent();
                dismissFont();
                break;
            case R.id.iv_alignleft:
                binding.richEditor.setAlignLeft();
                dismissFont();
                break;
            case R.id.iv_aligncenter:
                binding.richEditor.setAlignCenter();
                dismissFont();
                break;
            case R.id.iv_alignright:
                binding.richEditor.setAlignRight();
                dismissFont();
                break;
            case R.id.iv_color_red:
                binding.richEditor.setTextColor(Color.RED);
                dismissFont();
                break;
            case R.id.iv_color_green:
                binding.richEditor.setTextColor(Color.GREEN);
                dismissFont();
                break;
            case R.id.iv_color_yellow:
                binding.richEditor.setTextColor(Color.YELLOW);
                dismissFont();
                break;
            case R.id.iv_color_black:
                binding.richEditor.setTextColor(Color.BLACK);
                dismissFont();
                break;
            case R.id.iv_color_blue:
                binding.richEditor.setTextColor(Color.BLUE);
                dismissFont();
                break;
            case R.id.iv_color_darkblue:
                binding.richEditor.setTextColor(Color.DARK_BLUE);
                dismissFont();
                break;
            case R.id.iv_color_purple:
                binding.richEditor.setTextColor(Color.PURPLE);
                dismissFont();
                break;
            case R.id.iv_folder:
            case R.id.folder_name:
                FolderActivity.startActivityForResult(EditActivity.this, binding.folderName.getText().toString(), FolderActivity.REQUEST_CODE);
                break;
        }
    }
    //?????????????????????
    public void dismissFont(){
        binding.fontContainer.setVisibility(View.GONE);
        binding.editorContainer.setVisibility(View.VISIBLE);
        binding.richEditor.scrollToBottom();
        KeyBoardUtils.openKeybord(binding.editName, EditActivity.this);
    }

    private void againEdit() {
        //????????????????????????????????????????????????????????????????????????????????????
        binding.richEditor.focusEditor();
        KeyBoardUtils.openKeybord(binding.editName, EditActivity.this);
    }

    public void selectImage(int requestCode, ArrayList<ImageItem> imageItems) {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setCrop(false);
        imagePicker.setMultiMode(true);
        imagePicker.setShowCamera(true);
        Intent intent = new Intent(this, ImageGridActivity.class);
        intent.putExtra(ImageGridActivity.EXTRAS_IMAGES, imageItems);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (requestCode == 104) {
                selectImages.clear();
                ArrayList<ImageItem> selects = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                selectImages.addAll(selects);
                for (int i = 0; i < selectImages.size(); i++) {
//                    //?????????????????????????????????
//
//                    binding.richEditor.insertImage(selectImages.get(i).path, "dachshund");
                    //??????loading
                    showProgressDialog();
//                    if (!selectImages.isEmpty()) {
                        //??????????????????
                        ImageItem firstItem = selectImages.get(i);
                        String filename = firstItem.path.substring(firstItem.path.lastIndexOf("/") + 1);
                        Log.d("sakura", filename);
                        try {
                            AVFile avFile = AVFile.withAbsoluteLocalPath(filename, firstItem.path);
                            avFile.saveInBackground().subscribe(new Observer<AVFile>() {
                                @Override
                                public void onSubscribe(@NotNull Disposable d) {
                                }

                                @Override
                                public void onNext(@NotNull AVFile avFile) {
                                    againEdit();
                                    Log.d("sakura", "??????Url:" + avFile.getUrl());
                                    binding.richEditor.insertImage(avFile.getUrl(), "dachshund");
                                    KeyBoardUtils.openKeybord(binding.editName, EditActivity.this);
                                    binding.richEditor.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (binding.richEditor != null) {
                                                dialog.dismiss();
                                                binding.richEditor.scrollToBottom();
                                            }
                                        }
                                    }, 200);
                                }

                                @Override
                                public void onError(@NotNull Throwable e) {
                                    Log.d("sakura", "?????????????????????" + e.getMessage());
                                }

                                @Override
                                public void onComplete() {

                                }
                            });
                        } catch (FileNotFoundException e) {
                            Log.d("sakura", "FileNotFoundException" + e.getMessage());
                        }
                    }

//                for (int i = 0; i < selectImages.size(); i++) {
//                    //?????????????????????????????????
//
//                    binding.richEditor.insertImage(selectImages.get(i).path, "dachshund");
//                }
//                }
            }
        } else if (resultCode == -1) {
            if (requestCode == 11) {
                String outPath = data.getStringExtra(EXTRA_OUTPUT_URI);
                if (!TextUtils.isEmpty(outPath)) {
                    String newHtml = binding.richEditor.getHtml().replace(currentUrl, outPath);

                    binding.richEditor.setHtml(newHtml);
                    currentUrl = "";
                }
            }
        } else if (requestCode == FolderActivity.REQUEST_CODE) {
            if (resultCode == FolderActivity.Type.selectKind) {
                binding.folderName.setText(data.getStringExtra(FolderActivity.SELECT_KIND));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.undo:
                //??????
                binding.richEditor.undo();
                break;
            case R.id.redo:
                binding.richEditor.redo();
                break;
            case R.id.edit_finish:
                if (TextUtils.isEmpty(binding.richEditor.getHtml()) && TextUtils.isEmpty(binding.editName.getText())) {
                    Toast.makeText(EditActivity.this, "???????????????????????????", Toast.LENGTH_SHORT).show();
                } else {
                    if (isChange()) {
                        save();
                    }
                }
                binding.richEditor.clearFocusEditor();
                break;
            case R.id.tip:
                if (TextUtils.isEmpty(mNote.getTitle()) && TextUtils.isEmpty(mNote.getContent())){
                    Toast.makeText(EditActivity.this, "???????????????????????????,??????????????????",Toast.LENGTH_SHORT).show();
                } else if (timePicker != null){
                    timePicker.show();
                }
        }
        return true;
    }

    public void save() {
        long time = Utils.getCurrentTime();
        //??????????????????
        mNote.setTitle(TextUtils.isEmpty(binding.editName.getText().toString()) ?
                "???????????????" : binding.editName.getText().toString());
        mNote.setContent(binding.richEditor.getHtml());
        mNote.setKind(binding.folderName.getText().toString());
        //?????????????????????
        mNote.setUrl(HtmlUtil.getFirstUrlFromHtml(mNote.getContent()));
        if (mNote.getTime() > 0) {
            //??????????????????
            mViewModel.updateNode(mNote);
        } else {
            //??????????????????
            mNote.setTime(time);
            mViewModel.addNote(mNote);
        }
        Intent intent = new Intent();
        intent.putExtra("note", mNote);
        intent.putExtra("position", mPosition);
        setResult(200,intent);
    }

    private boolean isChange() {
        boolean isChange = false;
        if (mNote.getTitle() != null){
            isChange = !binding.editName.getText().toString().equals(mNote.getTitle());
        } else{
            isChange = !TextUtils.isEmpty(binding.editName.getText());
        }
        if (mNote.getContent() != null){
            isChange |= !binding.richEditor.getHtml().equals(mNote.getContent());
        } else{
            isChange |= !TextUtils.isEmpty(binding.richEditor.getHtml());
        }
        String s1 = binding.folderName.getText().toString();
        String s2 = mNote.getKind();
        return isChange || !binding.folderName.getText().toString().equals(mNote.getKind());

    }

    @Override
    public void onBackPressed() {
        if (isChange()) {
            //????????????????????????
            final AlertDialog.Builder builder =
                    new AlertDialog.Builder(EditActivity.this);
            builder.setTitle("?????????????");
            builder.setMessage("?????????????????????????????????????????????????");
            builder.setPositiveButton("??????",
                    (dialog, which) -> dialog.dismiss());
            builder.setNegativeButton("???",
                    (dialog, which) -> {
                        EditActivity.super.onBackPressed();
                    });
            builder.show();
        } else {
            super.onBackPressed();
        }
    }

    public void showProgressDialog(){
        if (dialog == null){
            dialog = new ProgressDialog(this);
            dialog.setCancelable(false);
            dialog.setMessage("??????????????????");
        }
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }
}
