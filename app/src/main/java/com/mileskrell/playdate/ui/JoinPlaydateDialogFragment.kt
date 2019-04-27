package com.mileskrell.playdate.ui

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.mileskrell.playdate.R
import kotlinx.android.synthetic.main.fragment_join_playdate.view.*

/**
 * @property callback Provided by [PlaydateListFragment]
 */
class JoinPlaydateDialogFragment(val callback: (code: String) -> Unit) : DialogFragment() {

    companion object {
        val remainingBase64Chars = listOf('+', '/', '=')
    }

    lateinit var dialogLayout: View

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogLayout = activity!!.layoutInflater.inflate(R.layout.fragment_join_playdate, null)

        val dialog = AlertDialog.Builder(context!!)
            .setView(dialogLayout)
            .setTitle(getString(R.string.join_playdate))
            .setPositiveButton(getString(R.string.join)) { _: DialogInterface, _: Int ->
                callback(dialogLayout.join_code_edit_text.text.toString())
            }
            .setNegativeButton(getString(R.string.cancel)) { _: DialogInterface, _: Int -> }
            .create()

        dialogLayout.join_code_edit_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).isEnabled = isPossiblePlaydateCode()

                dialogLayout.join_code_text_input_layout.run {
                    if (shouldDisplayError()) {
                        if (error == null) {
                            // If we set the error twice in quick succession, the error's display animation
                            // gets stuck until the error is cleared. This check prevents that.
                            error = getString(R.string.invalid_join_code)
                        }
                    } else {
                        error = null
                    }
                }
            }
        })

        return dialog
    }

    override fun onResume() {
        super.onResume()
        // Enable or disable "join" button on dialog open
        (dialog as AlertDialog).getButton(DialogInterface.BUTTON_POSITIVE).isEnabled = isPossiblePlaydateCode()
    }

    private fun isPossiblePlaydateCode(): Boolean {
        dialogLayout.join_code_edit_text.run {
            return text!!.isNotEmpty() && text!!.all { it.isBase64Char() }
        }
    }

    private fun shouldDisplayError() = dialogLayout.join_code_edit_text.text!!.any { !it.isBase64Char() }

    // TODO Are these the right characters?
    private fun Char.isBase64Char(): Boolean {
        return isLetterOrDigit() || remainingBase64Chars.contains(this)
    }
}
