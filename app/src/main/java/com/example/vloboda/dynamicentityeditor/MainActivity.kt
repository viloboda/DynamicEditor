package com.example.vloboda.dynamicentityeditor

import android.Manifest
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.DialogFragment.STYLE_NO_FRAME
import android.widget.Toast
import com.example.dal.Repository
import com.example.dal.RepositoryImpl
import com.example.model.FirmListDto
import com.example.model.serialization.GsonSerializer
import com.example.vloboda.dynamicentityeditor.dal.DataContextFactoryImpl
import android.support.v7.widget.*
import android.view.*
import com.example.bl.*
import com.example.bl.ui.DynamicToaster
import com.example.bl.ui.UICommonServices
import com.example.model.FirmDto
import com.example.vloboda.dynamicentityeditor.dynamic.DynamicAttributesFactoryImpl


class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    private val REQUEST_READ_ACCESS = 1

    private lateinit var inflater: LayoutInflater
    private lateinit var repository: Repository
    private lateinit var dynamicAttributesFactory: DynamicFieldsFactory
    private lateinit var fieldsConfigurationService: FieldsConfigurationService
    private lateinit var toaster: DynamicToaster
    private lateinit var editorTemplateManager: EditorTemplateManager
    private lateinit var locationManager: DymanicEditorLocationManager


    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        System.loadLibrary("sqliteX")

        AppState.AppDirectory = applicationContext.dataDir

        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.am_toolbar)
        setSupportActionBar(toolbar)

        inflater = LayoutInflater.from(this)

        toaster = DynamicToasterImpl(this)
        repository = RepositoryImpl(DataContextFactoryImpl(), GsonSerializer())
        locationManager = LocationManagerImpl()
        val commonServices = UICommonServices(LocationManagerImpl(), toaster)
        dynamicAttributesFactory = DynamicAttributesFactoryImpl(commonServices)
        fieldsConfigurationService = FieldsConfigurationServiceImpl(repository, toaster)
        editorTemplateManager = EditorTemplateManagerImpl(repository)

        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_READ_ACCESS)
    }

    private lateinit var rvAdapter: RvAdapter

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {

        // TODO тут надо бы проверить для начала, есть разрешения или нет

        for (result in grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "The application requires all requested permissions", Toast.LENGTH_LONG).show()
                return
            }
        }

        val recyclerView = findViewById<RecyclerView>(R.id.am_rv)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        rvAdapter = RvAdapter(inflater, FirmListDto(repository.getFirms(null)), this)
        recyclerView.adapter = rvAdapter

        val divider = DividerItemDecoration(this, layoutManager.orientation)
        recyclerView.addItemDecoration(divider)

        findViewById<View>(R.id.mv_add_button).setOnClickListener({
            val newFirm = FirmDto(repository.nextObjectId)
            newFirm.IsAddingMode = true
            val fm = supportFragmentManager
            val dialog = FirmEditableViewImpl.createView(newFirm, this)
            dialog.setStyle(STYLE_NO_FRAME, R.style.AppTheme)
            dialog.show(fm, null)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)

        val searchItem = menu.findItem(R.id.mm_searchBar)

        val searchView = searchItem.actionView as SearchView
        searchView.queryHint = "Поиск..."
        searchView.setOnQueryTextListener(this)
        searchView.isIconified = true
        searchView.maxWidth = Integer.MAX_VALUE

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.getItemId()

        return if (id == R.id.mm_searchBar) {
            true
        } else super.onOptionsItemSelected(item)

    }

    override fun onQueryTextChange(newText: String?): Boolean {
        rvAdapter.setItems(repository.getFirms(newText))

        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    fun getDynamicAttributesFactory(): DynamicFieldsFactory {
        return dynamicAttributesFactory
    }

    fun getFieldsConfigurationService(): FieldsConfigurationService {
        return fieldsConfigurationService
    }

    fun getEditorTemplateManager(): EditorTemplateManager {
        return editorTemplateManager
    }

    fun getLocationManager(): DymanicEditorLocationManager {
        return locationManager
    }

    fun getRepository(): Repository {
        return repository
    }

    fun getToaster(): DynamicToaster {
        return toaster
    }

    class RvAdapter(private val inflater: LayoutInflater,
                    private val dto: FirmListDto,
                    private val mainActivity: MainActivity) : RecyclerView.Adapter<ObjectSimpleView>() {

        override fun getItemCount(): Int {
            return if (dto.items == null) {
                0
            } else dto.items!!.size
        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ObjectSimpleView {

            return ObjectSimpleView.createView(viewGroup, inflater, dto.items[i])
        }

        override fun onBindViewHolder(cardView: ObjectSimpleView, i: Int) {
            cardView.initView(dto.items[i])
            cardView.itemView.setOnClickListener({ v ->
                run {
                    val fm = mainActivity.supportFragmentManager
                    val dialog = FirmEditableViewImpl.createView(v.getTag() as FirmDto, mainActivity)
                    dialog.setStyle(STYLE_NO_FRAME, R.style.AppTheme)
                    dialog.show(fm, null)
                }
            })
        }

        fun setItems(firms: List<FirmDto>) {
            dto.items = firms
            notifyDataSetChanged()
        }
    }

    fun refreshList() {
        rvAdapter.setItems(repository.getFirms(null))
        rvAdapter.notifyDataSetChanged()
    }
}
