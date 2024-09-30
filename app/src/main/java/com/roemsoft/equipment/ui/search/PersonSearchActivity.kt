package com.roemsoft.equipment.ui.search

import android.content.Intent
import android.view.Menu
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.roemsoft.common.hideSoftKeyboard
import com.roemsoft.common.widget.SpaceItemDecoration
import com.roemsoft.equipment.R
import com.roemsoft.equipment.databinding.ActivityPersonSearchBinding
import com.roemsoft.equipment.ui.DataBindingAppCompatActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@FlowPreview
@ExperimentalCoroutinesApi
class PersonSearchActivity : DataBindingAppCompatActivity() {

    companion object {
        const val EXTRA_SEARCH_TYPE = "extra:search:type"
        const val RESULT_SELECTED = "result:selected"
        const val ACTION_SEARCH_PERSON = 10003
    }

    private val dataBinding: ActivityPersonSearchBinding by binding(R.layout.activity_person_search)

    override val viewModel: PersonSearchViewModel by viewModels()

    override fun bindingView() {
        dataBinding.vm = viewModel
        dataBinding.lifecycleOwner = this
    }

    override fun getToolbar(): Toolbar {
        return dataBinding.toolbar
    }

    override fun setToolTitle() {
        dataBinding.toolbarTitle.text = getString(R.string.label_base_person)
    }

    override fun initToolbar() {
        super.initToolbar()
        dataBinding.toolbar.run {
            setNavigationOnClickListener {
                mSearchTextView?.let {
                    if (it.isFocused) {
                        it.clearFocus()
                        context.hideSoftKeyboard(this.windowToken)
                    } else {
                        finish()
                    }
                } ?: finish()
            }
        }
    }

    override fun initView() {
        dataBinding.refresh.apply {
            setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent)
        }

        dataBinding.recycler.run {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(SpaceItemDecoration(context))
        }

        viewModel.refreshData()
    }

    override fun setupEvent() {
        super.setupEvent()

        viewModel.select.observe(this) { result ->
            setResult(ACTION_SEARCH_PERSON, Intent().putExtra(RESULT_SELECTED, result))
            finish()
        }

        lifecycleScope.launch {
            viewModel.searchResult.collectLatest {
                viewModel.setAdapterData(it)
            }
        }
    }

    private var mSearchTextView: SearchView.SearchAutoComplete? = null

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_refresh_select, menu)
        val menuItem = menu.findItem(R.id.action_select_search)
        val searchView = menuItem.actionView as SearchView

        mSearchTextView = searchView.findViewById(androidx.appcompat.R.id.search_src_text)
        mSearchTextView?.doOnTextChanged { text, _, _, _ ->
            viewModel.search(text.toString().trim())
        }
        return true
    }
}