

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

        //var formatoDigital = `${fechaObjeto.getDate()}/${fechaObjeto.getMonth() + 1}/${fechaObjeto.getFullYear()}, ${fechaObjeto.getHours()}:${fechaObjeto.getMinutes()}`;
        var formatoDigital = fechaObjeto.toLocaleString();

        console.log(formatoDigital);
    }
}


changeFormat("11 abril @ 12:30 pm")
changeFormat("12 abril")
changeFormat("")