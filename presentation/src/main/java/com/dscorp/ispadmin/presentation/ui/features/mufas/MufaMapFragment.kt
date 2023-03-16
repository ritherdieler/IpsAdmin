import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.FragmentMufasMapBinding
import com.dscorp.ispadmin.presentation.ui.features.mufas.MufaDialogFragment
import com.dscorp.ispadmin.presentation.ui.features.mufas.MufaUiState
import com.dscorp.ispadmin.presentation.ui.features.mufas.MufaViewmodel
import com.example.cleanarchitecture.domain.domain.entity.Mufa
import com.example.cleanarchitecture.domain.domain.entity.NapBox
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MufaMapFragment : DialogFragment(), OnMapReadyCallback {
    lateinit var binding: FragmentMufasMapBinding
    val viewModel: MufaViewmodel by viewModel()
    private var mufas: List<Mufa> = emptyList()
    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var selectedLatLng: LatLng
    private val napBoxMarkersMap = mutableMapOf<Marker?, NapBox>()
    private val mufaMarkersMap = mutableMapOf<Marker?, Mufa>()


    override fun getTheme(): Int = R.style.Theme_IspAdminAndroid

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMufasMapBinding.inflate(inflater, container, false)
        observe()
        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        return binding.root
    }

    private fun observe() {
        lifecycleScope.launch {
            viewModel.mufaUiStateLiveData.observe(viewLifecycleOwner) {
                when (it) {
                    is MufaUiState.OnError -> {}
                    is MufaUiState.OnMufasListFound -> showMufasAsMakers(it.mufasList)
                }
            }
        }
    }

    private fun showMufasAsMakers(mufas: List<Mufa>) {
        mufas.forEach { mufa ->
            val latLng = LatLng((mufa.latitude ?: 0.0), (mufa.longitude ?: 0.0))
            val markerOptions = MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(R.drawable.ic_mufa)))
                .title(mufa.reference)
            val mufaMarker = googleMap.addMarker(markerOptions)?.apply {
                tag = "mufa"
            }
            mufaMarkersMap[mufaMarker] = mufa

            mufa.napBoxes?.forEach { napBox ->
                val napBoxLatLng = LatLng((napBox.latitude ?: 0.0), (napBox.longitude ?: 0.0))
                val napBoxMarkerOptions = MarkerOptions()
                    .position(napBoxLatLng)
                    .icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(R.drawable.ic_person)))
                val marker = googleMap.addMarker(napBoxMarkerOptions)?.apply {
                    tag = "napBox"

                }
                napBoxMarkersMap[marker] = napBox
            }
        }
    }


    private fun getBitmapFromVectorDrawable(drawableId: Int): Bitmap {
        val drawable = context?.let { ContextCompat.getDrawable(it, drawableId) }
        drawable?.let {
            val bitmap =
                Bitmap.createBitmap(it.intrinsicWidth, it.intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            it.setBounds(0, 0, canvas.width, canvas.height)
            it.draw(canvas)
            return bitmap
        }
        throw IllegalArgumentException("Invalid drawable passed.")
    }


    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.mapType = GoogleMap.MAP_TYPE_SATELLITE;
        val santaRosa = LatLng(-11.234996, -77.380347)
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(santaRosa))
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(11.5f))
        googleMap.setOnMarkerClickListener { marker ->
            if (marker.tag == "mufa") {
                val selectedMufa = mufaMarkersMap[marker]
                val action =
                    MufaMapFragmentDirections.actionNavMufaToMufaDialogFragment(selectedMufa!!)
                findNavController().navigate(action)
            } else {
                val selectedNapBox = napBoxMarkersMap[marker]
                val action =
                    MufaMapFragmentDirections.navMufaToNapBoxDialogFragment(selectedNapBox!!)
                findNavController().navigate(action)
            }

            true
        }


        googleMap.setOnCameraMoveListener {
            selectedLatLng = googleMap.cameraPosition.target
        }
    }


    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}