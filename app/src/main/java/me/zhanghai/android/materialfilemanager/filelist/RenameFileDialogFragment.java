/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.materialfilemanager.filelist;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import me.zhanghai.android.materialfilemanager.R;
import me.zhanghai.android.materialfilemanager.filesystem.File;
import me.zhanghai.android.materialfilemanager.util.FileNameUtils;
import me.zhanghai.android.materialfilemanager.util.FragmentUtils;

public class RenameFileDialogFragment extends FileNameDialogFragment {

    private static final String KEY_PREFIX = RenameFileDialogFragment.class.getName() + '.';

    private static final String EXTRA_FILE = KEY_PREFIX + "FILE";

    @NonNull
    private File mExtraFile;

    @NonNull
    public static RenameFileDialogFragment newInstance(@NonNull File file) {
        //noinspection deprecation
        RenameFileDialogFragment fragment = new RenameFileDialogFragment();
        FragmentUtils.getArgumentsBuilder(fragment)
                .putParcelable(EXTRA_FILE, file);
        return fragment;
    }

    public static void show(@NonNull File file, @NonNull Fragment fragment) {
        RenameFileDialogFragment.newInstance(file)
                .show(fragment.getChildFragmentManager(), null);
    }

    /**
     * @deprecated Use {@link #newInstance(File)} instead.
     */
    public RenameFileDialogFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mExtraFile = getArguments().getParcelable(EXTRA_FILE);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        if (savedInstanceState == null) {
            String name = mExtraFile.getName();
            mNameEdit.setText(name);
            int selectionEnd;
            if (mExtraFile.isDirectory()) {
                selectionEnd = name.length();
            } else {
                selectionEnd = FileNameUtils.indexOfExtensionSeparator(name);
                if (selectionEnd == -1) {
                    selectionEnd = name.length();
                }
            }
            mNameEdit.setSelection(0, selectionEnd);
        }
        return dialog;
    }

    @Override
    protected int getTitleRes() {
        return R.string.file_rename_title;
    }

    @Override
    protected boolean isNameUnchanged(@NonNull String name) {
        return TextUtils.equals(name, mExtraFile.getName());
    }

    @Override
    protected void onOk(@NonNull String name) {
        getListener().renameFile(mExtraFile, name);
    }

    @NonNull
    @Override
    protected Listener getListener() {
        return (Listener) getParentFragment();
    }

    public interface Listener extends FileNameDialogFragment.Listener {
        void renameFile(@NonNull File file, @NonNull String name);
    }
}
