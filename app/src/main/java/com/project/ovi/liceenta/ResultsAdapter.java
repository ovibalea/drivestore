/**
 * Copyright 2013 Google Inc. All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.project.ovi.liceenta;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.widget.DataBufferAdapter;

/**
 * A DataBufferAdapter to display the results of file listing/querying requests.
 */
public class ResultsAdapter extends DataBufferAdapter<Metadata> {

    public ResultsAdapter(Context context) {
        super(context, R.layout.file_row_layout);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(getContext(),
                    R.layout.file_row_layout, null);
        }
        Metadata metadata = getItem(position);
        TextView titleTextView =
                (TextView) convertView.findViewById(R.id.fileTextView);
        titleTextView.setText(metadata.getTitle());
        return convertView;
    }
}