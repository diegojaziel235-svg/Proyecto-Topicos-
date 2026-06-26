/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Vista;

import controlador.CCliente;
import java.util.ArrayList;
import modelo.Cliente;

/**
 *
 * @author jeshu
 */
public class PanelPrincipal extends javax.swing.JFrame {

    /**
     * Creates new form PanelPrincipal
     */
   private CCliente controlador = new CCliente();
        private javax.swing.table.DefaultTableModel modeloTabla;
        private int idClienteSeleccionado = -1;
        
    public PanelPrincipal() {
        initComponents();
        modeloTabla = (javax.swing.table.DefaultTableModel) tabla1.getModel();
        configurarEstadoInicial();
        configurarValidaciones();
        configurarEventos();
        configurarPlaceholders();
}
    
    // 1. Estado de botones solicitado por la profesora
private void configurarEstadoInicial() {
    btnGuardar.setEnabled(false);
    btnModificar.setEnabled(false);
    btnEliminar.setEnabled(false);
    btnAsignar.setEnabled(false); // Tu botón "Asignar Servicio"
    txtID.setEditable(true);       // Permitir escribir ID para nuevos registros
}

// 2. Monitorea que todos los campos estén llenos para activar el btnGuardar
private void validarCamposLenos() {
    String id = txtID.getText().trim();
    String nom = txtNombre.getText().trim();
    String ape = txtApellido.getText().trim();
    String tel = txtTelefono.getText().trim();

    boolean todosLenos = !id.isEmpty() && 
                         !nom.isEmpty() && !nom.equals("Ej: Juan") &&
                         !ape.isEmpty() && !ape.equals("Ej: Perez") &&
                         !tel.isEmpty() && !tel.equals("Ej: 9516543210");
    
    btnGuardar.setEnabled(todosLenos);
}

// 3. Filtros restrictivos por teclado (KeyEvents)
private void configurarValidaciones() {
    // txtID: Solo números
    txtID.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyTyped(java.awt.event.KeyEvent evt) {
            char c = evt.getKeyChar();
            if (!Character.isDigit(c)) {
                evt.consume(); 
            }
        }
        public void keyReleased(java.awt.event.KeyEvent evt) { validarCamposLenos(); }
    });

    // txtTelefono: Solo números
    // txtTelefono: Solo números y máximo 10 dígitos
        txtTelefono.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyTyped(java.awt.event.KeyEvent evt) {
        char c = evt.getKeyChar();
        // Si no es número, o si ya tiene 10 o más dígitos, bloquea la tecla
        if (!Character.isDigit(c) || txtTelefono.getText().trim().length() >= 10) {
            evt.consume();
            }
        }
        public void keyReleased(java.awt.event.KeyEvent evt) { validarCamposLenos(); }
    });

    // txtNombre: Solo letras, espacios y acentos válidos en español
    txtNombre.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyTyped(java.awt.event.KeyEvent evt) {
            char c = evt.getKeyChar();
            if (!Character.isLetter(c) && c != ' ' && "áéíóúÁÉÍÓÚñÑ".indexOf(c) == -1) {
                evt.consume();
            }
        }
        public void keyReleased(java.awt.event.KeyEvent evt) { validarCamposLenos(); }
    });

    // txtApellido: Solo letras, espacios y acentos válidos en español
    txtApellido.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyTyped(java.awt.event.KeyEvent evt) {
            char c = evt.getKeyChar();
            if (!Character.isLetter(c) && c != ' ' && "áéíóúÁÉÍÓÚñÑ".indexOf(c) == -1) {
                evt.consume();
            }
        }
        public void keyReleased(java.awt.event.KeyEvent evt) { validarCamposLenos(); }
    });
}

// 4. Listeners y manipulación con Excepciones de Control
private void configurarEventos() {

    // ACCIÓN: GUARDAR O ACTUALIZAR
    btnGuardar.addActionListener(e -> {
        try {
            int id = Integer.parseInt(txtID.getText().trim());
            String nombre = txtNombre.getText().trim();
            String apellido = txtApellido.getText().trim();
            String telefono = txtTelefono.getText().trim();

            Cliente existente = controlador.buscarPorId(id);

            if (existente == null) {
                // Es un registro nuevo
                Cliente nuevo = new Cliente(id, nombre, apellido, telefono);
                controlador.agregarCliente(nuevo);
                javax.swing.JOptionPane.showMessageDialog(this, "Cliente guardado exitosamente.");
            } else {
                // Estaba en modo modificación (se actualizan los valores sobre el mismo ID)
                existente.setNombre(nombre);
                existente.setApellido(apellido);
                existente.setTelefono(telefono);
                javax.swing.JOptionPane.showMessageDialog(this, "Cliente modificado con éxito.");
            }

            actualizarTabla(controlador.obtenerTodos());
            limpiarCampos();
            configurarEstadoInicial();

        } catch (NumberFormatException ex) {
            javax.swing.JOptionPane.showMessageDialog(this, "Error: El ID debe contener un formato numérico válido.", "Excepción Detectada", javax.swing.JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            javax.swing.JOptionPane.showMessageDialog(this, "Ocurrió un error inesperado al procesar: " + ex.getMessage(), "Error General", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    });

    // ACCIÓN: SELECCIONAR FILA DE LA TABLA
    // ACCIÓN: SELECCIONAR FILA DE LA TABLA (Solo guarda el ID y activa botones)
tabla1.addMouseListener(new java.awt.event.MouseAdapter() {
    public void mouseClicked(java.awt.event.MouseEvent evt) {
        try {
            int filaSeleccionada = tabla1.getSelectedRow();
            if (filaSeleccionada >= 0) {
                // Recuperamos el ID de la fila, pero NO lo ponemos en los txt todavía
                idClienteSeleccionado = Integer.parseInt(tabla1.getValueAt(filaSeleccionada, 0).toString());

                // Solo encendemos los botones de acción
                btnGuardar.setEnabled(false);
                btnModificar.setEnabled(true);
                btnEliminar.setEnabled(true);
                btnAsignar.setEnabled(true);
            }
        } catch (Exception ex) {
            javax.swing.JOptionPane.showMessageDialog(null, "Error al seleccionar: " + ex.getMessage(), "Excepción", javax.swing.JOptionPane.WARNING_MESSAGE);
        }
    }
});

// ACCIÓN: MODIFICAR (Aquí es donde se rellenan los datos automáticamente)
btnModificar.addActionListener(e -> {
    try {
        if (idClienteSeleccionado == -1) throw new Exception("Por favor, seleccione primero un cliente de la tabla.");

        // Buscamos los datos reales del cliente seleccionado usando su ID
        modelo.Cliente c = controlador.buscarPorId(idClienteSeleccionado);

        if (c != null) {
            // Rellenamos las cajas de texto automáticamente al dar clic en Modificar
            txtID.setText(String.valueOf(c.getIdCliente()));
            txtNombre.setText(c.getNombre());
            txtApellido.setText(c.getApellido());
            txtTelefono.setText(c.getTelefono());

            // Cambiamos los colores de la fuente a negro por si tenían el color gris del "Ej:"
            txtID.setForeground(java.awt.Color.BLACK);
            txtNombre.setForeground(java.awt.Color.BLACK);
            txtApellido.setForeground(java.awt.Color.BLACK);
            txtTelefono.setForeground(java.awt.Color.BLACK);

            // Bloqueamos ID para proteger la llave primaria y habilitamos guardar
            txtID.setEditable(false);
            btnGuardar.setEnabled(true); 
            
            // Apagamos los demás para obligar a guardar o cancelar
            btnModificar.setEnabled(false);
            btnEliminar.setEnabled(false);
            btnAsignar.setEnabled(false);
        }
    } catch (Exception ex) {
        javax.swing.JOptionPane.showMessageDialog(this, ex.getMessage(), "Control de Modificación", javax.swing.JOptionPane.WARNING_MESSAGE);
    }
});

    // ACCIÓN: ELIMINAR
    // ACCIÓN: ELIMINAR (CORREGIDO)
btnEliminar.addActionListener(e -> {
    try {
        // Validación con excepción: Si no ha seleccionado fila, idClienteSeleccionado será -1
        if (idClienteSeleccionado == -1) {
            throw new Exception("Por favor, seleccione primero un cliente de la tabla para eliminar.");
        }

        // Usamos la variable global directamente en lugar de la caja de texto vacía
        boolean eliminado = controlador.eliminarCliente(idClienteSeleccionado);

        if (!eliminado) {
            throw new Exception("El registro no pudo ser eliminado porque no se encontró en la lista.");
        }

        javax.swing.JOptionPane.showMessageDialog(this, "El cliente ha sido eliminado correctamente.");
        actualizarTabla(controlador.obtenerTodos());
        limpiarCampos();
        configurarEstadoInicial();
        
        // Resetemos la variable de selección global
        idClienteSeleccionado = -1; 
        actualizarContadorServicios(); 

    } catch (Exception ex) {
        javax.swing.JOptionPane.showMessageDialog(this, "Error al eliminar de la lista: " + ex.getMessage(), "Excepción", javax.swing.JOptionPane.ERROR_MESSAGE);
    }
});

// ACCIÓN: ASIGNAR SERVICIO (CORREGIDO)
btnAsignar.addActionListener(e -> {
    try {
        // Validación con excepción: Si no ha seleccionado fila
        if (idClienteSeleccionado == -1) {
            throw new Exception("Por favor, seleccione primero un cliente de la tabla para asignarle un servicio.");
        }

        // Usamos la variable global directamente
        modelo.Cliente c = controlador.buscarPorId(idClienteSeleccionado);

        if (c == null) {
            throw new NullPointerException("Imposible asignar servicio. Cliente inexistente.");
        }

        c.setServicioAsignado(true);
        javax.swing.JOptionPane.showMessageDialog(this, "¡Servicio asignado exitosamente al cliente!");
        
        actualizarContadorServicios();
        limpiarCampos();
        configurarEstadoInicial();
        
        // Reseteamos la variable de selección global
        idClienteSeleccionado = -1;

    } catch (Exception ex) {
        javax.swing.JOptionPane.showMessageDialog(this, "Error operacional: " + ex.getMessage(), "Excepción", javax.swing.JOptionPane.ERROR_MESSAGE);
    }
});

    // ACCIÓN: BUSCAR POR NOMBRE O TELÉFONO
    btnBuscar.addActionListener(e -> {
        String criterio = txtBuscar.getText().trim();
        if (criterio.isEmpty()) {
            actualizarTabla(controlador.obtenerTodos()); // Si está vacío muestra todo
        } else {
            ArrayList<Cliente> resultados = controlador.buscarPorNombreOTelefono(criterio);
            actualizarTabla(resultados);
        }
    });
}

// 5. Métodos Auxiliares del JFrame
private void actualizarTabla(ArrayList<Cliente> lista) {
    modeloTabla.setRowCount(0); // Vaciamos filas visuales anteriores
    for (Cliente c : lista) {
        modeloTabla.addRow(new Object[]{
            c.getIdCliente(),
            c.getNombre(),
            c.getApellido(),
            c.getTelefono()
        });
    }
}

private void actualizarContadorServicios() {
    int total = controlador.contarServiciosAsignados();
    lblCantidad.setText(String.valueOf(total));
}

private void limpiarCampos() {
    txtID.setText("");
    txtNombre.setText("");
    txtApellido.setText("");
    txtTelefono.setText("");
}

private void configurarPlaceholders() {
    // Configuración para txtNombre ("Ej: Juan")
    txtNombre.addFocusListener(new java.awt.event.FocusListener() {
        @Override
        public void focusGained(java.awt.event.FocusEvent e) {
            if (txtNombre.getText().equals("Ej: Juan")) {
                txtNombre.setText("");
                txtNombre.setForeground(java.awt.Color.BLACK); // Cambia a letra normal
            }
        }
        @Override
        public void focusLost(java.awt.event.FocusEvent e) {
            if (txtNombre.getText().trim().isEmpty()) {
                txtNombre.setText("Ej: Juan");
                txtNombre.setForeground(java.awt.Color.GRAY); // Color gris de sugerencia
            }
        }
    });

    // Configuración para txtApellido ("Ej: Perez")
    txtApellido.addFocusListener(new java.awt.event.FocusListener() {
        @Override
        public void focusGained(java.awt.event.FocusEvent e) {
            if (txtApellido.getText().equals("Ej: Perez")) {
                txtApellido.setText("");
                txtApellido.setForeground(java.awt.Color.BLACK);
            }
        }
        @Override
        public void focusLost(java.awt.event.FocusEvent e) {
            if (txtApellido.getText().trim().isEmpty()) {
                txtApellido.setText("Ej: Perez");
                txtApellido.setForeground(java.awt.Color.GRAY);
            }
        }
    });

    // Configuración para txtTelefono ("Ej: 9516543210")
    txtTelefono.addFocusListener(new java.awt.event.FocusListener() {
        @Override
        public void focusGained(java.awt.event.FocusEvent e) {
            if (txtTelefono.getText().equals("Ej: 9516543210")) {
                txtTelefono.setText("");
                txtTelefono.setForeground(java.awt.Color.BLACK);
            }
        }
        @Override
        public void focusLost(java.awt.event.FocusEvent e) {
            if (txtTelefono.getText().trim().isEmpty()) {
                txtTelefono.setText("Ej: 9516543210");
                txtTelefono.setForeground(java.awt.Color.GRAY);
            }
        }
    });

    // Configuración para txtBuscar ("Buscar Cliente")
    txtBuscar.addFocusListener(new java.awt.event.FocusListener() {
        @Override
        public void focusGained(java.awt.event.FocusEvent e) {
            if (txtBuscar.getText().equals("Buscar Cliente")) {
                txtBuscar.setText("");
                txtBuscar.setForeground(java.awt.Color.BLACK);
            }
        }
        @Override
        public void focusLost(java.awt.event.FocusEvent e) {
            if (txtBuscar.getText().trim().isEmpty()) {
                txtBuscar.setText("Buscar Cliente");
                txtBuscar.setForeground(java.awt.Color.GRAY);
            }
        }
    });
}
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jLabel7 = new javax.swing.JLabel();
        jDateChooser2 = new com.toedter.calendar.JDateChooser();
        jLabel8 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jTextField7 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jTextField4 = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        txtID = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        txtApellido = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        txtTelefono = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        btnGuardar = new javax.swing.JButton();
        btnModificar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        btnAsignar = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        lblCantidad = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabla1 = new javax.swing.JTable();
        jPanel9 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        txtBuscar = new javax.swing.JTextField();
        btnBuscar = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel10.setText("Estado");

        jLabel4.setText("Vehiculo ");

        jLabel6.setText("Fecha Ingreso ");

        jLabel7.setText("Fecha de salida ");

        jLabel8.setText("Costo final ");

        jLabel1.setText("       DATOS DE ÓRDEN");
        jLabel1.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jLabel2.setText("ID ORDEN");

        jLabel3.setText("Cliente");

        jLabel9.setText("Observaciones");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Cliente", "Vehiculo", "Servicio", "Ingreso", "Costo", "Estado"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jButton5.setText("GUARDAR");

        jButton6.setText("LIMPIAR ");

        jButton7.setText("ELIMINAR");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel9)
                                        .addGap(476, 476, 476))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(17, 17, 17)
                                                .addComponent(jLabel10)))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(60, 60, 60)
                                        .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(48, 48, 48)))
                                .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel7)
                                            .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(10, 10, 10)
                                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(31, 31, 31)
                                                .addComponent(jLabel3))
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(25, 25, 25)
                                                .addComponent(jLabel4))
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(12, 12, 12)
                                                .addComponent(jLabel6))
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(26, 26, 26)
                                                .addComponent(jLabel2)))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jDateChooser1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE)
                                            .addComponent(jDateChooser2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGap(23, 23, 23)))
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 645, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(27, 27, 27))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel6)
                        .addGap(21, 21, 21)
                        .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jDateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel8)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel9))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(26, 26, 26)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 346, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField4)
                .addGap(18, 18, 18))
        );

        jTabbedPane1.addTab("ORDEN", jPanel1);

        jPanel2.setBackground(new java.awt.Color(244, 246, 250));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 102, 204));
        jLabel5.setText("Administración");

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel11.setText("Gestione su base de clientes activos");

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Datos del cliente", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14))); // NOI18N

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel12.setText("ID Cliente");

        txtID.setBackground(new java.awt.Color(235, 240, 249));

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel13.setText("Nombre");

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel14.setText("Apellido");

        txtNombre.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        txtNombre.setForeground(new java.awt.Color(102, 102, 102));
        txtNombre.setText("Ej: Juan");

        txtApellido.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        txtApellido.setForeground(new java.awt.Color(102, 102, 102));
        txtApellido.setText("Ej: Perez");

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel15.setText("Teléfono");

        txtTelefono.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        txtTelefono.setForeground(new java.awt.Color(102, 102, 102));
        txtTelefono.setText("Ej: 9516543210");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel15)
                    .addComponent(jLabel12)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel14))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtApellido, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtTelefono)
                    .addComponent(txtID, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(16, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel12)
                .addGap(12, 12, 12)
                .addComponent(txtID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtApellido, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                    .addComponent(txtNombre))
                .addGap(18, 18, 18)
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(39, Short.MAX_VALUE))
        );

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Acciones", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14))); // NOI18N

        btnGuardar.setBackground(new java.awt.Color(46, 204, 113));
        btnGuardar.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnGuardar.setForeground(new java.awt.Color(255, 255, 255));
        btnGuardar.setText("Guardar");

        btnModificar.setBackground(new java.awt.Color(243, 156, 18));
        btnModificar.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnModificar.setForeground(new java.awt.Color(255, 255, 255));
        btnModificar.setText("Modificar");

        btnEliminar.setBackground(new java.awt.Color(231, 76, 60));
        btnEliminar.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnEliminar.setForeground(new java.awt.Color(255, 255, 255));
        btnEliminar.setText("Eliminar");

        btnAsignar.setBackground(new java.awt.Color(0, 102, 153));
        btnAsignar.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnAsignar.setForeground(new java.awt.Color(255, 255, 255));
        btnAsignar.setText("Asignar Servicio");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnAsignar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnEliminar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel6Layout.createSequentialGroup()
                        .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnModificar, javax.swing.GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnModificar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnGuardar, javax.swing.GroupLayout.DEFAULT_SIZE, 64, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEliminar, javax.swing.GroupLayout.DEFAULT_SIZE, 64, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAsignar, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));

        jPanel8.setBackground(new java.awt.Color(235, 240, 249));

        jLabel16.setForeground(new java.awt.Color(0, 102, 204));
        jLabel16.setText("REGISTROS ACTUALES");

        lblCantidad.setForeground(new java.awt.Color(0, 0, 102));
        lblCantidad.setText("20");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblCantidad)
                .addGap(25, 25, 25))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(lblCantidad))
                .addContainerGap(9, Short.MAX_VALUE))
        );

        tabla1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "ID", "NOMBRE", "APELLIDO", "TELEFONO"
            }
        ));
        jScrollPane2.setViewportView(tabla1);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 468, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 385, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));

        jLabel17.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel17.setText("Búsqueda rápida (Nombre o teléfono)");

        txtBuscar.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtBuscar.setForeground(new java.awt.Color(102, 102, 102));
        txtBuscar.setText("Buscar Cliente");

        btnBuscar.setBackground(new java.awt.Color(0, 102, 153));
        btnBuscar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnBuscar.setForeground(new java.awt.Color(255, 255, 255));
        btnBuscar.setText("Buscar");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel17)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 338, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(34, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnBuscar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtBuscar, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE))
                .addContainerGap(18, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 828, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel5)
                        .addComponent(jLabel11)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addGap(10, 10, 10)
                                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGap(10, 10, 10)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 612, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jLabel5)
                    .addGap(4, 4, 4)
                    .addComponent(jLabel11)
                    .addGap(23, 23, 23)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(20, 20, 20)
                            .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(10, 10, 10)
                            .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        jTabbedPane1.addTab("CLIENTES", jPanel2);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 828, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 612, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("VEHICULOS", jPanel3);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 828, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 612, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("SERVICIOS", jPanel4);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 828, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
    /* Establecer el diseño estético Nimbus por defecto */
    try {
        for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
                javax.swing.UIManager.setLookAndFeel(info.getClassName());
                break;
            }
        }
    } catch (Exception ex) {
        java.util.logging.Logger.getLogger(PanelPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }

    /* Arrancar y hacer visible la ventana principal de las pestañas */
    java.awt.EventQueue.invokeLater(new Runnable() {
        public void run() {
            // REEMPLAZA "NombreDeTuVentanaPrincipal" por el nombre real de tu JFrame con pestañas
            new PanelPrincipal().setVisible(true);
        }
    });
}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAsignar;
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnModificar;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private com.toedter.calendar.JDateChooser jDateChooser2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JLabel lblCantidad;
    private javax.swing.JTable tabla1;
    private javax.swing.JTextField txtApellido;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtID;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtTelefono;
    // End of variables declaration//GEN-END:variables
}
