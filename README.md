CREAR BASE DE DATOS Reservas;

USE Reservas; 

CREATE TABLE IF NOT EXISTS clientes ( 

  idCliente INT PRIMARY KEY AUTO_INCREMENT, 

  nombre VARCHAR(100) NOT NULL, 

  contacto VARCHAR(100) NOT NULL, 

  preferencias TEXT 

); 

  

CREATE TABLE IF NOT EXISTS habitaciones ( 

  idHabitacion INT PRIMARY KEY, 

  tipo ENUM('Estándar', 'Deluxe', 'Suite') NOT NULL, 

  estado ENUM('Disponible', 'Ocupada', 'En limpieza', 'En mantenimiento', 'Reservada', 'Confirmada') NOT NULL 

); 

  

USE Reservas;  

CREATE TABLE IF NOT EXISTS reservas (  

  

  idReserva INT AUTO_INCREMENT PRIMARY KEY,  

  idCliente INT,  

  idHabitacion INT, 

  habitacion Varchar(10), 

  numero_habitacion Varchar(10) NOT NULL,  -- Cambiado de ENUM a VARCHAR para flexibilidad  

  tipo_habitacion ENUM('Estándar', 'Deluxe', 'Suite') NOT NULL,  

  fecha_entrada DATE NOT NULL,  

  fecha_salida DATE NOT NULL,  

  estado ENUM('Disponible', 'Ocupada', 'En limpieza', 'En mantenimiento', 'Reservada', 'Confirmada', 'Cancelada', 'Finalizada') NOT NULL,  

  FOREIGN KEY (idCliente) REFERENCES clientes(idCliente) ON DELETE CASCADE ON UPDATE CASCADE,  

  FOREIGN KEY (idHabitacion) REFERENCES habitaciones(idHabitacion) ON DELETE CASCADE ON UPDATE CASCADE  

); 
 
INSERT INTO habitaciones (idHabitacion, tipo, estado)  

VALUES  

(1, 'Estándar',  'Ocupada'),(2, 'Estándar',  'Disponible'),(3, 'Estándar',  'Disponible'),  

(4, 'Estándar',  'Disponible'),(5, 'Estándar',  'Ocupada'),(6, 'Estándar',  'Disponible'), 

(7, 'Estándar',  'Disponible'),(8, 'Estándar',  'Disponible'),(9, 'Estándar',  'Disponible'), 

(10, 'Estándar', 'Disponible'),(11, 'Estándar', 'Ocupada'),(12, 'Estándar', 'Disponible'), 

(13, 'Estándar', 'Disponible'),(14, 'Estándar', 'Disponible'),(15, 'Estándar', 'Disponible'), 

(16, 'Estándar', 'Disponible'),(17, 'Estándar', 'Ocupada'),(18, 'Estándar', 'Disponible'), 

(19, 'Estándar', 'Disponible'),(20, 'Estándar', 'Ocupada'),(21, 'Estándar', 'Disponible'), 

(22, 'Estándar', 'Disponible'),(23, 'Estándar', 'Ocupada'),(24, 'Estándar', 'Disponible'), 

(25, 'Estándar', 'Disponible'),(26, 'Deluxe', 'Disponible'),(27, 'Deluxe', 'Disponible'), 

(28, 'Deluxe', 'Disponible'),(29, 'Deluxe', 'Disponible'),(30, 'Deluxe', 'Disponible'), 

(31, 'Deluxe', 'Ocupada'),(32, 'Deluxe', 'Disponible'),(33, 'Deluxe', 'Disponible'), 

(34, 'Deluxe', 'Disponible'),(35, 'Deluxe', 'Disponible'),(36, 'Suite', 'Ocupada'), 

(37, 'Suite', 'Disponible'),(38, 'Suite', 'Disponible'),(39, 'Suite', 'Disponible'), 

(40, 'Suite', 'Ocupada'),(41, 'Suite', 'Disponible'),(42, 'Suite', 'Disponible'), 

(43, 'Suite', 'Disponible'),(44, 'Suite', 'Ocupada'),(45, 'Suite', 'Disponible'), 

(46, 'Suite', 'Disponible'),(47, 'Suite', 'Disponible'),(48, 'Suite', 'Disponible'), 

(49, 'Suite', 'Disponible'),(50, 'Suite', 'Ocupada'); 
