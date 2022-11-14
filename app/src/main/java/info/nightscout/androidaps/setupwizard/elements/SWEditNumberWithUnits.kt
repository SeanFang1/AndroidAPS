package info.nightscout.androidaps.setupwizard.elements

import android.graphics.Typeface
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import dagger.android.HasAndroidInjector
import info.nightscout.androidaps.setupwizard.SWNumberValidator
import info.nightscout.androidaps.utils.ui.NumberPicker
import info.nightscout.core.profile.toCurrentUnits
import info.nightscout.interfaces.GlucoseUnit
import info.nightscout.interfaces.profile.Profile
import info.nightscout.interfaces.profile.ProfileFunction
import info.nightscout.shared.SafeParse
import java.text.DecimalFormat
import javax.inject.Inject

class SWEditNumberWithUnits(injector: HasAndroidInjector, private val init: Double, private val min: Double, private val max: Double) : SWItem(injector, Type.UNIT_NUMBER) {

    @Inject lateinit var profileFunction: ProfileFunction

    private val validator: SWNumberValidator = SWNumberValidator { value -> value in min..max }
    private var updateDelay = 0

    override fun generateDialog(layout: LinearLayout) {
        val context = layout.context
        val watcher: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (validator.isValid(SafeParse.stringToDouble(s.toString())))
                    save(s.toString(), updateDelay.toLong())
            }

            override fun afterTextChanged(s: Editable) {}
        }

        val l = TextView(context)
        l.id = View.generateViewId()
        label?.let { l.setText(it) }
        l.setTypeface(l.typeface, Typeface.BOLD)
        layout.addView(l)
        var initValue = sp.getDouble(preferenceId, init)
        initValue = Profile.toCurrentUnits(profileFunction.getUnits(), initValue)
        val numberPicker = NumberPicker(context)
        if (profileFunction.getUnits() == GlucoseUnit.MMOL) numberPicker.setParams(initValue, min, max, 0.1, DecimalFormat("0.0"), false, null, watcher) else numberPicker.setParams(initValue, min * 18, max * 18, 1.0, DecimalFormat("0"), false, null, watcher)

        layout.addView(numberPicker)
        val c = TextView(context)
        c.id = View.generateViewId()
        comment?.let { c.setText(it) }
        c.setTypeface(c.typeface, Typeface.ITALIC)
        layout.addView(c)
        super.generateDialog(layout)
    }

    fun preferenceId(preferenceId: Int): SWEditNumberWithUnits {
        this.preferenceId = preferenceId
        return this
    }

    fun updateDelay(updateDelay: Int): SWEditNumberWithUnits {
        this.updateDelay = updateDelay
        return this
    }

}