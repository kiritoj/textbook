package com.leo.copytoutiao.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.Toolbar;

import com.leo.copytoutiao.R;
import com.leo.copytoutiao.databinding.ActivityPublishBinding;
import com.leo.copytoutiao.utils.KeyBoardUtils;
import com.leo.copytoutiao.utils.RichUtils;
import com.leo.copytoutiao.utils.popup.CommonPopupWindow;
import com.leo.copytoutiao.view.RichEditor;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.taoke.base.BaseActivity;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yalantis.ucrop.UCropActivity;
import com.leo.copytoutiao.utils.Color;


import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import static com.yalantis.ucrop.UCrop.EXTRA_OUTPUT_URI;

/**
 * 富文本编辑页面
 */
public class EditActivity extends BaseActivity implements View.OnClickListener {
    ActivityPublishBinding binding;
    RxPermissions rxPermissions;
    private ArrayList<ImageItem> selectImages = new ArrayList<>();

    private CommonPopupWindow popupWindow; //编辑图片的pop
    private String currentUrl = "";

    private int isFrom;//0:表示正常编辑  1:表示是重新编辑

    //编辑item按下后，图标选择。由于框架无法及时回调造成的
    private boolean mIsItalicSelect = false;
    private int mThreshold;
    private int mCurProgress = 25; //seekBar当前值


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_publish);
        isFrom = getIntent().getIntExtra("isFrom", 0);
        binding.setOnClickListener(this);
        rxPermissions = new RxPermissions(this);
        initPop();
        initEditor();
        //initThreshold();
        initViews();
        if (isFrom == 1) {
            SharedPreferences sharedPreferences = getSharedPreferences("art", MODE_PRIVATE);
            String title = sharedPreferences.getString("title", "title");
            String content = sharedPreferences.getString("content", "");
            binding.editName.setText(title);
            binding.richEditor.setHtml(content);
        }
        initToolBar(findViewById(R.id.toolbar), "编辑", true, -1);
    }


    private void initEditor() {
        //输入框显示字体的大小
        binding.richEditor.setEditorFontSize(18);
        //输入框显示字体的颜色
        binding.richEditor.setEditorFontColor(getResources().getColor(R.color.black));
        //输入框背景设置
        binding.richEditor.setEditorBackgroundColor(Color.WHITE);
        //输入框文本padding
        binding.richEditor.setPadding(10, 10, 10, 10);
        //输入提示文本
        binding.richEditor.setPlaceholder("请开始你的创作！~");

        //文本输入框监听事件
        binding.richEditor.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
            @Override
            public void onTextChange(String text) {
//                Log.e("富文本文字变动", text);
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

        view.findViewById(R.id.linear_editor).setOnClickListener(v -> {
            //编辑图片

            rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE).subscribe(aBoolean -> {
                if (aBoolean) {
                    if (ActivityCompat.checkSelfPermission(EditActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(EditActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                        Intent intent = new Intent(EditActivity.this, UCropActivity.class);
                        intent.putExtra("filePath", currentUrl);
                        String destDir = getFilesDir().getAbsolutePath().toString();
                        String fileName = "SampleCropImage" + System.currentTimeMillis() + ".jpg";
                        intent.putExtra("outPath", destDir + fileName);
                        startActivityForResult(intent, 11);
                        popupWindow.dismiss();

                    }
                } else {
                    Toast.makeText(EditActivity.this, "相册需要此权限", Toast.LENGTH_SHORT).show();
                }
            });
        });

        view.findViewById(R.id.linear_delete_pic).setOnClickListener(v -> {
            //删除图片

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
                .setOutsideTouchable(true)//在外不可用手指取消
                .setAnimationStyle(R.style.pop_animation)//设置popWindow的出场动画
                .create();


        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                binding.richEditor.setInputEnabled(true);
            }
        });
    }

    private void initThreshold() {
        //根据layout长度判断软键盘是否弹起
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
                            // 说明键盘是弹出状态
                            binding.richEditor.focusEditor();
                            binding.fontContainer.setVisibility(View.GONE);
                            binding.editorContainer.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    private void initViews() {
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
                //编辑器字体四级起步
                binding.richEditor.setFontSize(size + 3);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_bold:
                //加粗
                againEdit();
                binding.richEditor.setBold();
                break;

            case R.id.button_underline:
                //加下划线
                againEdit();
                binding.richEditor.setUnderline();
                break;

            case R.id.button_list_ul:
                //加带点的序列号
                againEdit();
                binding.richEditor.setBullets();
                break;

            case R.id.button_list_ol:
                //加带数字的序列号
                againEdit();
                binding.richEditor.setNumbers();
                break;
            case R.id.button_italic:
                //斜体
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
                        Toast.makeText(EditActivity.this, "最多添加9张照片~", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(EditActivity.this, "相册需要此权限~", Toast.LENGTH_SHORT).show();

                    }
                });
                break;
            //调节字体颜色，大小，左缩进，又缩进，左对齐，右对齐，居中对齐
            case R.id.button_font:
                binding.fontContainer.setVisibility(View.VISIBLE);
                binding.editorContainer.setVisibility(View.GONE);
                KeyBoardUtils.closeKeybord(binding.editName, EditActivity.this);
                break;

            //文本调整部分点击事件
            case R.id.font_cancel:
                binding.fontContainer.setVisibility(View.GONE);
                binding.editorContainer.setVisibility(View.VISIBLE);
                KeyBoardUtils.openKeybord(binding.editName, EditActivity.this);
                break;
            case R.id.iv_indent:
                binding.richEditor.setIndent();
                break;
            case R.id.iv_outdent:
                binding.richEditor.setOutdent();
                break;
            case R.id.iv_alignleft:
                binding.richEditor.setAlignLeft();
                break;
            case R.id.iv_aligncenter:
                binding.richEditor.setAlignCenter();
                break;
            case R.id.iv_alignright:
                binding.richEditor.setAlignRight();
                break;
            case R.id.iv_color_red:
                binding.richEditor.setTextColor(Color.RED);
                break;
            case R.id.iv_color_green:
                binding.richEditor.setTextColor(Color.GREEN);
                break;
            case R.id.iv_color_yellow:
                binding.richEditor.setTextColor(Color.YELLOW);
                break;
            case R.id.iv_color_black:
                binding.richEditor.setTextColor(Color.BLACK);
                break;
            case R.id.iv_color_blue:
                binding.richEditor.setTextColor(Color.BLUE);
                break;
            case R.id.iv_color_darkblue:
                binding.richEditor.setTextColor(Color.DARK_BLUE);
                break;
            case R.id.iv_color_purple:
                binding.richEditor.setTextColor(Color.PURPLE);
                break;
        }
    }

    private void againEdit() {
        //如果第一次点击例如加粗，没有焦点时，获取焦点并弹出软键盘
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

                againEdit();
                for (int i = 0; i < selectImages.size(); i++) {
                    binding.richEditor.insertImage(selectImages.get(i).path, "dachshund");
                }
                KeyBoardUtils.openKeybord(binding.editName, EditActivity.this);
                binding.richEditor.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (binding.richEditor != null) {
                            binding.richEditor.scrollToBottom();
                        }
                    }
                }, 200);
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
                //撤销
                binding.richEditor.undo();
                break;
            case R.id.redo:
                binding.richEditor.redo();
                break;
            case R.id.edit_finish:
                SharedPreferences sharedPreferences = getSharedPreferences("art", MODE_PRIVATE);
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putString("content", binding.richEditor.getHtml());
                edit.putString("title", binding.editName.getText().toString().trim());
                edit.commit();
                Toast.makeText(this, "发布成功", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, PreViewActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        return true;
    }
}
