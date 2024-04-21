

function changeFormat(fecha) {
    if (fecha) {
        fecha = fecha.split('@');
        var fechaPartes = fecha[0].split(' ');
        var dia = fechaPartes[0];
        var mes = fechaPartes[1];
        var hora = fecha[1] ? fecha[1].trim() : '';

        var meses = { 'enero': 0,'febrero': 1, 'marzo': 2, 'abril': 3, 'mayo': 4, 'junio': 5, 'julio': 6,
            'agosto': 7, 'septiembre': 8, 'octubre': 9, 'noviembre': 10, 'diciembre': 11
        };
        
        var mesNumero = meses[mes.toLowerCase()];

        var fechaObjeto = new Date();
        fechaObjeto.setDate(parseInt(dia));
        fechaObjeto.setMonth(mesNumero); 
        fechaObjeto.setHours(hora ? parseInt(hora.split(':')[0]) : 0);
        fechaObjeto.setMinutes(hora ? parseInt(hora.split(':')[1]) : 0);
        fechaObjeto.setSeconds(0);
        if (hora.includes("pm")) {
            fechaObjeto.setHours(fechaObjeto.getHours() + 12);
        }

        var fechaFormateada = fechaObjeto.getFullYear() + '-' +
            ('0' + (fechaObjeto.getMonth() + 1)).slice(-2) + '-' +
            ('0' + fechaObjeto.getDate()).slice(-2) + ', ' +
            ('0' + fechaObjeto.getHours()).slice(-2) + ':' +
            ('0' + fechaObjeto.getMinutes()).slice(-2) + ':' +
            ('0' + fechaObjeto.getSeconds()).slice(-2);

        console.log(fechaFormateada);
        
        return fechaFormateada;
    }
}


changeFormat("11 noviembre @ 12:30 pm")
changeFormat("12 noviembre")
changeFormat("")