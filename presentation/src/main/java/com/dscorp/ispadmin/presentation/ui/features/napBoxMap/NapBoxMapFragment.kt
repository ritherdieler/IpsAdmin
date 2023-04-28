import NapBoxMapFragmentDirections.*
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.FragmentNapboxMapBinding
import com.dscorp.ispadmin.presentation.ui.features.base.BaseFragment
import com.dscorp.ispadmin.presentation.ui.features.mufas.MufaUiState
import com.dscorp.ispadmin.presentation.ui.features.mufas.MufaViewModel
import com.dscorp.ispadmin.presentation.ui.features.mufas.NapBoxDetailDialogFragment
import com.example.cleanarchitecture.domain.domain.entity.Mufa
import com.example.cleanarchitecture.domain.domain.entity.NapBoxResponse
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch

class NapBoxMapFragment : BaseFragment<MufaUiState, FragmentNapboxMapBinding>(), OnMapReadyCallback,
    NapBoxDetailDialogFragment.NapBoxSelectionListener {
    override val binding by lazy { FragmentNapboxMapBinding.inflate(layoutInflater) }
    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var selectedLatLng: LatLng
    private val napBoxMarkersMap = mutableMapOf<Marker?, NapBoxResponse>()
    override val viewModel: MufaViewModel by viewModels()

    override fun handleState(state: MufaUiState) {
        when (state) {
            is MufaUiState.OnMufasListFound -> showMufasAsMakers(state.mufasList)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        return binding.root
    }

    private fun showMufasAsMakers(mufas: List<Mufa>) {
        mufas.forEach { mufa ->

            mufa.napBoxes?.forEach { napBox ->
                val napBoxLatLng = LatLng((napBox.latitude ?: 0.0), (napBox.longitude ?: 0.0))
                val napBoxMarkerOptions = MarkerOptions()
                    .position(napBoxLatLng)
                    .icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(R.drawable.ic_napbox)))
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

    @SuppressLint("MissingPermission")
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.isMyLocationEnabled = true
        googleMap.mapType = GoogleMap.MAP_TYPE_HYBRID;
        val santaRosa = LatLng(-11.234996, -77.380347)
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(santaRosa))
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(11.5f))
        googleMap.setOnMarkerClickListener { marker ->
            NapBoxDetailDialogFragment(
                napBox = napBoxMarkersMap[marker]!!,
                showSelectButton = true,
                listener = this
            ).show(childFragmentManager, NapBoxDetailDialogFragment::class.java.name)

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

    override fun onNapBoxSelected(napBox: NapBoxResponse) {
        setFragmentResult(
            NAP_BOX_SELECTION_RESULT,
            Bundle().apply { putSerializable(NAP_BOX_OBJECT, napBox) })
        findNavController().popBackStack(R.id.nav_subscription, false)

    }

    companion object {
        const val NAP_BOX_OBJECT = "NAP_BOX_OBJECT"
        const val NAP_BOX_SELECTION_RESULT = "NAP_BOX_SELECTION_RESULT"
    }

}