
package proyectotopicos;

import java.util.ArrayList;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class ControladorOrden {
  
    ArrayList<Orden> listaOrden = new ArrayList<>();

    private static final Pattern PFECHA = Pattern.compile("^\\d{2}/\\d{2}/\\d{4}$");
    private static final Pattern PCOSTO = Pattern.compile("^[0-9]+(\\.[0-9]{1,2})?$");
    DefaultTableModel mod;
    
   public void guardar(PanelPrincipal panel) {

        try {
            String idOrden = String.format("ORD-%03d", listaOrden.size() + 1);
            panel.Id_orden.setText(idOrden);
            panel.Id_orden.setEditable(false);

            String clienteTexto  = panel.cliente.getText().trim();
            String vehiculoTexto = panel.vehiculo.getText().trim();
            String costoTexto    = panel.Cfinal.getText().trim();
            String obs           = panel.observaciones.getText().trim();
            int idServicio = panel.jComboBox1.getSelectedIndex() + 1;

            if (clienteTexto.isEmpty() || vehiculoTexto.isEmpty() || costoTexto.isEmpty()) {
                throw new Exception("Por favor llena todos los campos obligatorios");
            }

            if (!PCOSTO.matcher(costoTexto).matches()) {
                throw new Exception("El costo debe ser un número válido");
            }

            int    idCliente  = Integer.parseInt(clienteTexto);
            int    idVehiculo = Integer.parseInt(vehiculoTexto);
            double costoFinal = Double.parseDouble(costoTexto);

            if (panel.Fingreso.getDate() == null || panel.Fsalida.getDate() == null) {
                throw new Exception("Selecciona las fechas de ingreso y salida.");
            }

            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
            String fechaIngreso = sdf.format(panel.Fingreso.getDate());
            String fechaSalida  = sdf.format(panel.Fsalida.getDate());

            if (!PFECHA.matcher(fechaIngreso).matches() || !PFECHA.matcher(fechaSalida).matches()) {
                throw new Exception("El formato de fecha no es válido.");
            }

            Orden nuevaOrden = new Orden(idOrden, fechaIngreso, fechaSalida, costoFinal,
                                         idServicio, idCliente, idVehiculo, obs);
            listaOrden.add(nuevaOrden);

            DefaultTableModel modelo = (DefaultTableModel) panel.jTable1.getModel();
            modelo.addRow(new Object[]{
                idOrden, idCliente, idVehiculo, idServicio, fechaIngreso, costoFinal, "Activa"
            });

            JOptionPane.showMessageDialog(panel, "Orden " + idOrden + " guardada correctamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);

            limpiar(panel);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(panel, "Error de formato numérico: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(panel, e.getMessage(), "Error de validación", JOptionPane.WARNING_MESSAGE);
        }
    }
   
    public void tablaVe(JTable tabla){
        mod=new DefaultTableModel();
        mod.addColumn("ID Auto");
        mod.addColumn("Marca");
        mod.addColumn("Modelo");
        mod.addColumn("Color");
        mod.addColumn("Tipo");
        tabla.setModel(mod);
    }

    public void limpiar(PanelPrincipal panel) {
         panel.Id_orden.setText("");
         panel.cliente.setText("");
         panel.vehiculo.setText("");
         panel.Cfinal.setText("");
         panel.observaciones.setText("");
         panel.Fingreso.setDate(null);
         panel.Fsalida.setDate(null);
         panel.jComboBox1.setSelectedIndex(0);
    }

    public void eliminar(PanelPrincipal panel) {

        try {
            int filaSeleccionada = panel.jTable1.getSelectedRow();

            if (filaSeleccionada == -1) {
                throw new Exception("Selecciona una orden de la tabla para eliminarla.");
            }

            int respuesta = JOptionPane.showConfirmDialog(panel,
                    "¿Seguro que quieres eliminar esta orden?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION);

            if (respuesta == JOptionPane.YES_OPTION) {
                DefaultTableModel modelo = (DefaultTableModel) panel.jTable1.getModel();
                String idOrdenEliminar = modelo.getValueAt(filaSeleccionada, 0).toString();
                listaOrden.removeIf(o -> o.getIdOrden().equals(idOrdenEliminar));
                modelo.removeRow(filaSeleccionada);
                JOptionPane.showMessageDialog(panel, "Orden eliminada.", "Listo", JOptionPane.INFORMATION_MESSAGE);
                limpiar(panel);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(panel, "No se pudo leer el ID de la orden.",
                    "Error", JOptionPane.ERROR_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(panel, e.getMessage(), "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    // Carga los datos de la fila seleccionada en los campos del formulario
public void cargarDatos(PanelPrincipal panel) {
    int fila = panel.jTable1.getSelectedRow();
    if (fila == -1) return;

    DefaultTableModel modelo = (DefaultTableModel) panel.jTable1.getModel();

    panel.Id_orden.setText(modelo.getValueAt(fila, 0).toString());
    panel.cliente.setText(modelo.getValueAt(fila, 1).toString());
    panel.vehiculo.setText(modelo.getValueAt(fila, 2).toString());
    panel.Cfinal.setText(modelo.getValueAt(fila, 5).toString());
}

// Actualiza la orden seleccionada con los datos editados
public void actualizar(PanelPrincipal panel) {

    try {
        int fila = panel.jTable1.getSelectedRow();

        if (fila == -1) {
            throw new Exception("Selecciona una orden de la tabla para actualizar.");
        }

        String clienteTexto  = panel.cliente.getText().trim();
        String vehiculoTexto = panel.vehiculo.getText().trim();
        String costoTexto    = panel.Cfinal.getText().trim();
        String obs           = panel.observaciones.getText().trim();
        int idServicio = panel.jComboBox1.getSelectedIndex() + 1;

        if (clienteTexto.isEmpty() || vehiculoTexto.isEmpty() || costoTexto.isEmpty()) {
            throw new Exception("Por favor llena todos los campos obligatorios.");
        }

        if (!PCOSTO.matcher(costoTexto).matches()) {
            throw new Exception("El costo debe ser un número válido. Ej: 200 o 350.50");
        }

        if (panel.Fingreso.getDate() == null || panel.Fsalida.getDate() == null) {
            throw new Exception("Selecciona las fechas de ingreso y salida.");
        }

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
        String fechaIngreso = sdf.format(panel.Fingreso.getDate());
        String fechaSalida  = sdf.format(panel.Fsalida.getDate());

        int    idCliente  = Integer.parseInt(clienteTexto);
        int    idVehiculo = Integer.parseInt(vehiculoTexto);
        double costoFinal = Double.parseDouble(costoTexto);

        // Obtener el ID de la orden seleccionada
        DefaultTableModel modelo = (DefaultTableModel) panel.jTable1.getModel();
        String idOrden = modelo.getValueAt(fila, 0).toString();

        // Actualizar en el ArrayList
        for (Orden o : listaOrden) {
            if (o.getIdOrden().equals(idOrden)) {
                o.setIdCliente(idCliente);
                o.setIdVehiculo(idVehiculo);
                o.setCostoFinal(costoFinal);
                o.setIdServicio(idServicio);
                o.setFechaIngreso(fechaIngreso);
                o.setFechaSalida(fechaSalida);
                o.setObservaciones(obs);
                break;
            }
        }

        // Actualizar la fila en la tabla
        modelo.setValueAt(idCliente,   fila, 1);
        modelo.setValueAt(idVehiculo,  fila, 2);
        modelo.setValueAt(idServicio,  fila, 3);
        modelo.setValueAt(fechaIngreso,fila, 4);
        modelo.setValueAt(costoFinal,  fila, 5);

        JOptionPane.showMessageDialog(panel, "Orden " + idOrden + " actualizada correctamente.",
                "Éxito", JOptionPane.INFORMATION_MESSAGE);

        limpiar(panel);

    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(panel, "Error de formato numérico: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);

    } catch (Exception e) {
        JOptionPane.showMessageDialog(panel, e.getMessage(), "Error de validación", JOptionPane.WARNING_MESSAGE);
    }
}
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}